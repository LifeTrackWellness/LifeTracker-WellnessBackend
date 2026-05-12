package com.wellness.backend.repository;


import com.wellness.backend.enums.AlertStatus;
import com.wellness.backend.enums.AlertType;
import com.wellness.backend.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    // Todas las alertas de un profesional ordenadas por fecha
    List<Alert> findByProfessionalIdOrderByCreatedAtDesc(Long professionalId);

    // Alertas pendientes de un profesional
    List<Alert> findByProfessionalIdAndStatusOrderByCreatedAtDesc(
            Long professionalId, AlertStatus status);

    // Cantidad de alertas pendientes
    Long countByProfessionalIdAndStatus(Long professionalId, AlertStatus status);

    // Verificar si ya existe una alerta pendiente del mismo tipo para ese paciente
    boolean existsByPatientIdAndTypeAndStatus(
            Long patientId, AlertType type, AlertStatus status);
}
