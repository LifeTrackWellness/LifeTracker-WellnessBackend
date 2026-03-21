package com.wellness.backend.service;

import com.wellness.backend.dto.request.DeactivatePatientRequest;
import com.wellness.backend.dto.request.PatientListDTO;
import com.wellness.backend.dto.request.ReactivatePatientRequest;
import com.wellness.backend.enums.PatientStatus;
import com.wellness.backend.exception.BusinessException;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.Patient;
import com.wellness.backend.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // CRITERIO: Registrar un paciente con validación de documento único
    @Transactional
    public Patient createPatient(Patient patient) {
        if (patientRepository.existsByIdentityDocument(patient.getIdentityDocument())) {
            throw new BusinessException(
                    "Error: El documento de identidad " + patient.getIdentityDocument() + " ya está registrado.");
        }
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

    // Obtener paciente por id
    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
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
                p.getStatus()
        )).collect(Collectors.toList());
    }



}
