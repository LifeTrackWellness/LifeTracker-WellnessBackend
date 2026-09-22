package com.compa.repository;

import com.compa.enums.NotificationStatus;
import com.compa.enums.NotificationType;
import com.compa.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByEstudianteIdOrderByCreatedAtDesc(Long estudianteId);

    long countByEstudianteIdAndStatusNot(Long estudianteId, NotificationStatus status);

    Optional<Notification> findByActionToken(String actionToken);

    boolean existsByEstudianteIdAndTypeAndCreatedAtBetween(
            Long estudianteId, NotificationType type, LocalDateTime from, LocalDateTime to);
}