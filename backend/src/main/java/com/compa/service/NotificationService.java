package com.compa.service;

import com.compa.enums.NotificationChannel;
import com.compa.enums.NotificationStatus;
import com.compa.enums.NotificationType;
import com.compa.exception.BusinessException;
import com.compa.exception.ResourceNotFoundException;
import com.compa.model.HabitTask;
import com.compa.model.Notification;
import com.compa.model.Estudiante;
import com.compa.model.TaskCheckIn;
import com.compa.repository.HabitTaskRepository;
import com.compa.repository.NotificationRepository;
import com.compa.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    // El link de acción del correo vence a las 48h
    private static final long ACTION_TOKEN_EXPIRATION_HOURS = 48;

    private final NotificationRepository notificationRepository;
    private final EstudianteRepository estudianteRepository;
    private final HabitTaskRepository habitTaskRepository;
    private final EmailService emailService;
    private final DailyCheckInService dailyCheckInService;

    public Notification createAndSend(Long estudianteId, NotificationType type,
                                      NotificationChannel channel, String title, String message) {
        return createAndSend(estudianteId, type, channel, title, message, null);
    }

    public Notification createAndSend(Long estudianteId, NotificationType type,
                                      NotificationChannel channel, String title, String message,
                                      Long relatedTaskId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", estudianteId));

        if (relatedTaskId != null) {
            HabitTask task = habitTaskRepository.findById(relatedTaskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tarea", relatedTaskId));
            if (!task.getHabitPlan().getEstudiante().getId().equals(estudianteId)) {
                throw new ResourceNotFoundException("Tarea", relatedTaskId);
            }
        }

        Notification.NotificationBuilder builder = Notification.builder()
                .estudiante(estudiante)
                .type(type)
                .channel(channel)
                .status(NotificationStatus.PENDIENTE)
                .title(title)
                .message(message)
                .relatedTaskId(relatedTaskId);

        if (relatedTaskId != null) {
            builder.actionToken(UUID.randomUUID().toString())
                    .actionTokenExpiresAt(LocalDateTime.now().plusHours(ACTION_TOKEN_EXPIRATION_HOURS));
        }

        Notification notification = notificationRepository.save(builder.build());

        boolean sendEmail = channel == NotificationChannel.EMAIL
                || channel == NotificationChannel.IN_APP_Y_EMAIL;

        if (sendEmail && estudiante.getEmail() != null) {
            try {
                if (notification.getActionToken() != null) {
                    emailService.sendTaskReminderEmail(
                            estudiante.getEmail(),
                            estudiante.getName(),
                            title,
                            message,
                            notification.getActionToken()
                    );
                } else {
                    emailService.sendNotificationEmail(
                            estudiante.getEmail(),
                            estudiante.getName(),
                            title,
                            message
                    );
                }
            } catch (Exception e) {
                log.error("Error enviando email de notificación a {}: {}", estudiante.getEmail(), e.getMessage());
            }
        }

        notification.setStatus(NotificationStatus.ENVIADA);
        notification.setSentAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public boolean alreadySentToday(Long estudianteId, NotificationType type) {
        LocalDate today = LocalDate.now();
        return notificationRepository.existsByEstudianteIdAndTypeAndCreatedAtBetween(
                estudianteId, type, today.atStartOfDay(), today.atTime(23, 59, 59));
    }

    @Transactional(readOnly = true)
    public List<Notification> getByEstudiante(Long estudianteId) {
        return notificationRepository.findByEstudianteIdOrderByCreatedAtDesc(estudianteId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long estudianteId) {
        return notificationRepository.countByEstudianteIdAndStatusNot(estudianteId, NotificationStatus.LEIDA);
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación", notificationId));
        notification.setStatus(NotificationStatus.LEIDA);
        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(Long estudianteId) {
        List<Notification> pending = notificationRepository.findByEstudianteIdOrderByCreatedAtDesc(estudianteId);
        LocalDateTime now = LocalDateTime.now();
        for (Notification n : pending) {
            if (n.getStatus() != NotificationStatus.LEIDA) {
                n.setStatus(NotificationStatus.LEIDA);
                n.setReadAt(now);
            }
        }
        notificationRepository.saveAll(pending);
    }

    // ---- HU-03: marcar tarea cumplida directamente desde la notificación ----

    // Ruta autenticada (in-app): el estudiante ya tiene sesión
    public Notification completeTaskFromNotification(Long notificationId, Long estudianteId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación", notificationId));

        if (!notification.getEstudiante().getId().equals(estudianteId)) {
            throw new ResourceNotFoundException("Notificación", notificationId);
        }

        return applyTaskCompletion(notification);
    }

    // Ruta pública (link de correo, sin sesión iniciada)
    public Notification completeTaskFromActionToken(String actionToken) {
        Notification notification = notificationRepository.findByActionToken(actionToken)
                .orElseThrow(() -> new ResourceNotFoundException("El enlace no es válido"));

        if (!notification.isActionTaken()
                && notification.getActionTokenExpiresAt() != null
                && LocalDateTime.now().isAfter(notification.getActionTokenExpiresAt())) {
            throw new BusinessException("Este enlace ya venció. Abre la app para marcar la tarea como cumplida.");
        }

        return applyTaskCompletion(notification);
    }

    private Notification applyTaskCompletion(Notification notification) {
        if (notification.getRelatedTaskId() == null) {
            throw new BusinessException("Esta notificación no está asociada a una tarea");
        }

        // Idempotente: si ya se accionó, no se vuelve a tocar el check-in
        if (!notification.isActionTaken()) {
            TaskCheckIn taskCheckIn = dailyCheckInService.markTaskCompleted(
                    notification.getEstudiante().getId(), notification.getRelatedTaskId());

            notification.setActionTaken(true);
            notification.setActionTakenAt(LocalDateTime.now());
            if (notification.getStatus() != NotificationStatus.LEIDA) {
                notification.setStatus(NotificationStatus.LEIDA);
                notification.setReadAt(LocalDateTime.now());
            }
            notification = notificationRepository.save(notification);
            log.info(">>> Tarea {} marcada como cumplida vía notificación {} (task_check_in {})",
                    notification.getRelatedTaskId(), notification.getId(), taskCheckIn.getId());
        }

        return notification;
    }
}