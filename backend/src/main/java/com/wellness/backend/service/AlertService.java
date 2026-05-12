package com.wellness.backend.service;

import com.wellness.backend.enums.AlertStatus;
import com.wellness.backend.enums.AlertType;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.Alert;
import com.wellness.backend.model.Patient;
import com.wellness.backend.model.Professional;
import com.wellness.backend.repository.AlertRepository;
import com.wellness.backend.repository.PatientRepository;
import com.wellness.backend.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;
    private final PatientRepository patientRepository;
    private final ProfessionalRepository professionalRepository;
    private final EmailService emailService;

    public void createAlertIfNotExists(Long patientId, AlertType type, String description) {
        log.info(">>> Intentando crear alerta tipo {} para paciente {}", type, patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        if (patient.getProfessional() == null) {
            log.warn(">>> Paciente {} no tiene profesional asignado", patientId);
            return;
        }

        Professional professional = professionalRepository
                .findById(patient.getProfessional().getId())
                .orElse(null);

        if (professional == null) {
            log.warn(">>> Profesional no encontrado para paciente {}", patientId);
            return;
        }

        boolean alreadyExists = alertRepository.existsByPatientIdAndTypeAndStatus(
                patientId, type, AlertStatus.PENDIENTE);

        log.info(">>> Ya existe alerta? {}", alreadyExists);

        if (alreadyExists) return;

        Alert alert = Alert.builder()
                .patient(patient)
                .professional(professional)
                .type(type)
                .status(AlertStatus.PENDIENTE)
                .description(description)
                .build();

        alertRepository.save(alert);
        log.info(">>> Alerta {} guardada para paciente {}", type, patientId);

        if (type == AlertType.RIESGO_ALTO) {
            try {
                emailService.sendRiskAlertEmail(
                        professional.getEmail(),
                        professional.getName(),
                        patient.getName() + " " + patient.getLastName(),
                        description
                );
            } catch (Exception e) {
                log.error("Error enviando email de alerta: {}", e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Alert> getAlertsByProfessional(Long professionalId) {
        return alertRepository.findByProfessionalIdOrderByCreatedAtDesc(professionalId);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long professionalId) {
        return alertRepository.countByProfessionalIdAndStatus(
                professionalId, AlertStatus.PENDIENTE);
    }

    public Alert resolveAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta", alertId));
        alert.setStatus(AlertStatus.RESUELTA);
        alert.setResolvedAt(LocalDateTime.now());
        return alertRepository.save(alert);
    }

    public Alert ignoreAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta", alertId));
        alert.setStatus(AlertStatus.IGNORADA);
        alert.setResolvedAt(LocalDateTime.now());
        return alertRepository.save(alert);
    }
}