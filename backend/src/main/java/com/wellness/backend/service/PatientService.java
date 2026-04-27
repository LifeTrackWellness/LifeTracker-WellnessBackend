package com.wellness.backend.service;

import com.wellness.backend.dto.request.CreatePatientAccountRequest;
import com.wellness.backend.dto.request.DeactivatePatientRequest;
import com.wellness.backend.dto.request.PatientListDTO;
import com.wellness.backend.dto.request.ReactivatePatientRequest;
import com.wellness.backend.enums.PatientStatus;
import com.wellness.backend.enums.Role;
import com.wellness.backend.exception.BusinessException;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.Patient;
import com.wellness.backend.model.Professional;
import com.wellness.backend.repository.PatientRepository;
import com.wellness.backend.repository.ProfessionalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final ConsentService consentService;
    private final ProfessionalRepository professionalRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PatientService(PatientRepository patientRepository,
            ConsentService consentService,
            ProfessionalRepository professionalRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.patientRepository = patientRepository;
        this.consentService = consentService;
        this.professionalRepository = professionalRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // CRITERIO: Registrar un paciente con validación de documento único
    @Transactional
    public Patient createPatient(Patient patient) {
        if (patientRepository.existsByIdentityDocument(patient.getIdentityDocument())) {
            throw new BusinessException(
                    "Error: El documento de identidad " + patient.getIdentityDocument() + " ya está registrado.");
        }
        Patient savedPatient = patientRepository.save(patient);
        consentService.generateConsentsForPatient(savedPatient); // ← línea nueva
        return savedPatient;
    }

    // CRITERIO: El profesional crea una cuenta de paciente desde su panel
    // El sistema genera contraseña temporal y envía email de activación
    @Transactional
    public Patient createPatientAccount(Long professionalId, CreatePatientAccountRequest request) {
        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", professionalId));

        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un paciente con ese email");
        }

        // Generar contraseña temporal y token de activación
        String tempPassword = generateTempPassword();
        String activationToken = UUID.randomUUID().toString();

        Patient patient = new Patient();
        patient.setName(request.getName());
        patient.setLastName(request.getLastName());
        patient.setEmail(request.getEmail());
        patient.setPassword(passwordEncoder.encode(tempPassword));
        patient.setTempPassword(tempPassword);
        patient.setActivationToken(activationToken);
        patient.setActivationTokenExpiresAt(LocalDateTime.now().plusHours(48));
        patient.setAccountActivated(false);
        patient.setProfessional(professional);
        patient.setIdentityDocument(UUID.randomUUID().toString()); // temporal hasta que complete perfil
        patient.setStatus(PatientStatus.ACTIVO);
        patient.setRole(Role.PATIENT);

        patient = patientRepository.save(patient);
        consentService.generateConsentsForPatient(patient);

        // Enviar email con credenciales al paciente
        try {
            emailService.sendPatientCredentials(
                    patient.getEmail(),
                    patient.getName(),
                    tempPassword,
                    activationToken);
        } catch (Exception e) {
            log.warn("No se pudo enviar email a {}: {}", patient.getEmail(), e.getMessage());
        }

        return patient;
    }

    // CRITERIO: El paciente activa su cuenta desde el link recibido por email
    @Transactional
    public Patient activatePatientAccount(String token) {
        Patient patient = patientRepository.findByActivationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido o ya usado"));

        if (patient.getActivationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El enlace de activación ha vencido.");
        }

        patient.setAccountActivated(true);
        patient.setActivationToken(null);
        patient.setActivationTokenExpiresAt(null);
        patient.setTempPassword(null);

        return patientRepository.save(patient);
    }

    // CRITERIO: El terapeuta puede editar datos de contacto (correo, celular)
    @Transactional
    public Patient updateContactInfo(Long id, String newEmail, String newPhone) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
        patient.setEmail(newEmail);
        patient.setPhoneNumber(newPhone);
        return patientRepository.save(patient);
    }

    // Método adicional para listar activos (útil para el frontend en React)
    public List<Patient> getAllPatients() {
        return patientRepository.findByStatus(PatientStatus.ACTIVO);
    }

    // Listar pacientes inactivos
    public List<Patient> getInactivePatients() {
        return patientRepository.findByStatus(PatientStatus.INACTIVO);
    }

    // Listar pacientes vinculados a un profesional específico
    public List<Patient> getPatientsByProfessional(Long professionalId) {
        return patientRepository.findByProfessionalId(professionalId);
    }

    // Obtener paciente por id
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
    }

    // Desactivar paciente (baja lógica) - requiere motivo
    @Transactional
    public Patient deactivatePatient(Long id, DeactivatePatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
        if (PatientStatus.INACTIVO.equals(patient.getStatus())) {
            throw new BusinessException("El paciente ya se encuentra inactivo.");
        }
        patient.setStatus(PatientStatus.INACTIVO);
        patient.setDeactivationReason(request.getReason());
        patient.setDeactivatedAt(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    // Reactivar paciente - permite actualizar info básica
    @Transactional
    public Patient reactivatePatient(Long id, ReactivatePatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
        if (PatientStatus.ACTIVO.equals(patient.getStatus())) {
            throw new BusinessException("El paciente ya se encuentra activo.");
        }
        patient.setStatus(PatientStatus.ACTIVO);
        patient.setDeactivationReason(null);
        patient.setDeactivatedAt(null);
        if (request.getName() != null && !request.getName().isBlank())
            patient.setName(request.getName());
        if (request.getLastName() != null && !request.getLastName().isBlank())
            patient.setLastName(request.getLastName());
        if (request.getDocumentType() != null)
            patient.setDocumentType(request.getDocumentType());
        if (request.getPhoneNumber() != null)
            patient.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null)
            patient.setEmail(request.getEmail());
        return patientRepository.save(patient);
    }

    public List<PatientListDTO> getAllPatientsFiltered(String search, PatientStatus status, String condition) {
        List<Patient> patients;

        if (search != null && !search.isEmpty()) {
            patients = patientRepository.findByNameContainingIgnoreCaseOrIdentityDocumentContaining(search, search);
        } else if (condition != null && !condition.isEmpty()) {
            patients = patientRepository.findByPrimaryCondition(condition);
        } else if (status != null) {
            patients = patientRepository.findByStatus(status);
        } else {
            patients = patientRepository.findAll();
        }

        return patients.stream().map(p -> new PatientListDTO(
                p.getName(),
                p.getIdentityDocument(),
                (p.getClinicalInfo() != null) ? p.getClinicalInfo().getMainCondition() : "Sin asignar",
                p.getStatus())).collect(Collectors.toList());
    }

    // Genera una contraseña temporal aleatoria de 10 caracteres
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}