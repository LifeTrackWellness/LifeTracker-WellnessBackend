package com.wellness.backend.service;

import com.wellness.backend.dto.request.GuardianRequest;
import com.wellness.backend.dto.response.GuardianResponse;
import com.wellness.backend.enums.DocumentType;
import com.wellness.backend.exception.BusinessException;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.Guardian;
import com.wellness.backend.model.Patient;
import com.wellness.backend.repository.GuardianRepository;
import com.wellness.backend.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private final PatientRepository patientRepository;

    public GuardianService(GuardianRepository guardianRepository, PatientRepository patientRepository) {
        this.guardianRepository = guardianRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public GuardianResponse saveGuardian(Long patientId, GuardianRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        if (patient.getDocumentType() != DocumentType.TARJETA_DE_IDENTIDAD) {
            throw new BusinessException(
                    "El acudiente solo aplica para pacientes con Tarjeta de Identidad. " +
                            "Este paciente tiene Cédula de Ciudadanía.");
        }

        Guardian guardian = guardianRepository.findByPatientId(patientId)
                .orElse(new Guardian());

        guardian.setName(request.getName());
        guardian.setLastName(request.getLastName());
        guardian.setDocumentType(request.getDocumentType());
        guardian.setIdentityDocument(request.getIdentityDocument());
        guardian.setRelationship(request.getRelationship());
        guardian.setEmail(request.getEmail());
        guardian.setPhoneNumber(request.getPhoneNumber());
        guardian.setPatient(patient);

        Guardian saved = guardianRepository.save(guardian);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public GuardianResponse getGuardianByPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        Guardian guardian = guardianRepository.findByPatientId(patientId)
                .orElseThrow(() -> new BusinessException(
                        "El paciente con ID " + patientId + " no tiene acudiente registrado."));

        return toResponse(guardian);
    }

    @Transactional
    public void deleteGuardian(Long patientId) {
        Guardian guardian = guardianRepository.findByPatientId(patientId)
                .orElseThrow(() -> new BusinessException(
                        "El paciente con ID " + patientId + " no tiene acudiente registrado."));
        guardianRepository.delete(guardian);
    }

    private GuardianResponse toResponse(Guardian g) {
        return new GuardianResponse(
                g.getId(),
                g.getName(),
                g.getLastName(),
                g.getDocumentType(),
                g.getIdentityDocument(),
                g.getRelationship(),
                g.getEmail(),
                g.getPhoneNumber(),
                g.getPatient().getId());
    }

}
