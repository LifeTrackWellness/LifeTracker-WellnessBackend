package com.compa.repository;


import com.compa.enums.AlertStatus;
import com.compa.enums.AlertType;
import com.compa.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    // Todas las alertas de un orientador ordenadas por fecha
    List<Alert> findByOrientadorIdOrderByCreatedAtDesc(Long orientadorId);

    // Alertas pendientes de un orientador
    List<Alert> findByOrientadorIdAndStatusOrderByCreatedAtDesc(
            Long orientadorId, AlertStatus status);

    // Cantidad de alertas pendientes
    Long countByOrientadorIdAndStatus(Long orientadorId, AlertStatus status);

    // Verificar si ya existe una alerta pendiente del mismo tipo para ese estudiante
    boolean existsByEstudianteIdAndTypeAndStatus(
            Long estudianteId, AlertType type, AlertStatus status);
}
