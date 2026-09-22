package com.compa.service;

import com.compa.enums.AlertStatus;
import com.compa.enums.AlertType;
import com.compa.exception.ResourceNotFoundException;
import com.compa.model.Alert;
import com.compa.model.Estudiante;
import com.compa.model.Orientador;
import com.compa.repository.AlertRepository;
import com.compa.repository.EstudianteRepository;
import com.compa.repository.OrientadorRepository;
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
    private final EstudianteRepository estudianteRepository;
    private final OrientadorRepository orientadorRepository;
    private final EmailService emailService;

    public void createAlertIfNotExists(Long estudianteId, AlertType type, String description) {
        log.info(">>> Intentando crear alerta tipo {} para estudiante {}", type, estudianteId);

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", estudianteId));

        if (estudiante.getOrientador() == null) {
            log.warn(">>> Estudiante {} no tiene orientador asignado", estudianteId);
            return;
        }

        Orientador orientador = orientadorRepository
                .findById(estudiante.getOrientador().getId())
                .orElse(null);

        if (orientador == null) {
            log.warn(">>> Orientador no encontrado para estudiante {}", estudianteId);
            return;
        }

        boolean alreadyExists = alertRepository.existsByEstudianteIdAndTypeAndStatus(
                estudianteId, type, AlertStatus.PENDIENTE);

        log.info(">>> Ya existe alerta? {}", alreadyExists);

        if (alreadyExists) return;

        Alert alert = Alert.builder()
                .estudiante(estudiante)
                .orientador(orientador)
                .type(type)
                .status(AlertStatus.PENDIENTE)
                .description(description)
                .build();

        alertRepository.save(alert);
        log.info(">>> Alerta {} guardada para estudiante {}", type, estudianteId);

        if (type == AlertType.RIESGO_ALTO) {
            try {
                emailService.sendRiskAlertEmail(
                        orientador.getEmail(),
                        orientador.getName(),
                        estudiante.getName() + " " + estudiante.getLastName(),
                        description
                );
            } catch (Exception e) {
                log.error("Error enviando email de alerta: {}", e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Alert> getAlertsByOrientador(Long orientadorId) {
        return alertRepository.findByOrientadorIdOrderByCreatedAtDesc(orientadorId);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long orientadorId) {
        return alertRepository.countByOrientadorIdAndStatus(
                orientadorId, AlertStatus.PENDIENTE);
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