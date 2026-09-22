package com.compa.controller;

import com.compa.dto.request.SendNotificationRequest;
import com.compa.dto.response.NotificationResponse;
import com.compa.enums.NotificationType;
import com.compa.exception.ResourceNotFoundException;
import com.compa.model.Notification;
import com.compa.model.Estudiante;
import com.compa.repository.EstudianteRepository;
import com.compa.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final EstudianteRepository estudianteRepository;

    // ---- Endpoints del estudiante (self-service) ----

    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        Estudiante estudiante = getEstudianteFromToken(userDetails);
        List<NotificationResponse> response = notificationService.getByEstudiante(estudiante.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/unread-count")
    public ResponseEntity<Map<String, Long>> getMyUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        Estudiante estudiante = getEstudianteFromToken(userDetails);
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(estudiante.getId())));
    }

    @PatchMapping("/me/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        getEstudianteFromToken(userDetails);
        return ResponseEntity.ok(toResponse(notificationService.markAsRead(id)));
    }

    @PatchMapping("/me/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        Estudiante estudiante = getEstudianteFromToken(userDetails);
        notificationService.markAllAsRead(estudiante.getId());
        return ResponseEntity.noContent().build();
    }

    // ---- Endpoints del orientador (sobre un estudiante puntual) ----

    @GetMapping("/estudiantes/{estudianteId}")
    public ResponseEntity<List<NotificationResponse>> getEstudianteNotifications(
            @PathVariable Long estudianteId) {
        List<NotificationResponse> response = notificationService.getByEstudiante(estudianteId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/estudiantes/{estudianteId}")
    public ResponseEntity<NotificationResponse> sendManualMessage(
            @PathVariable Long estudianteId,
            @Valid @RequestBody SendNotificationRequest request) {
        Notification notification = notificationService.createAndSend(
                estudianteId,
                NotificationType.MENSAJE_ORIENTADOR,
                request.getChannel(),
                request.getTitle(),
                request.getMessage(),
                request.getTaskId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(notification));
    }

    // ---- HU-03: marcar tarea cumplida directamente desde la notificación ----

    // Ruta autenticada: usada desde la campana/panel de notificaciones dentro de la app
    @PatchMapping("/me/{id}/complete-task")
    public ResponseEntity<NotificationResponse> completeTaskFromApp(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Estudiante estudiante = getEstudianteFromToken(userDetails);
        Notification notification = notificationService.completeTaskFromNotification(id, estudiante.getId());
        return ResponseEntity.ok(toResponse(notification));
    }

    // Ruta pública: usada por el botón del correo, sin sesión iniciada
    @PostMapping("/actions/{token}/complete-task")
    public ResponseEntity<NotificationResponse> completeTaskFromEmailLink(
            @PathVariable String token) {
        Notification notification = notificationService.completeTaskFromActionToken(token);
        return ResponseEntity.ok(toResponse(notification));
    }

    // ---- Helpers ----

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .estudianteId(n.getEstudiante().getId())
                .estudianteName(n.getEstudiante().getName() + " " + n.getEstudiante().getLastName())
                .type(n.getType())
                .channel(n.getChannel())
                .status(n.getStatus())
                .title(n.getTitle())
                .message(n.getMessage())
                .createdAt(n.getCreatedAt())
                .sentAt(n.getSentAt())
                .readAt(n.getReadAt())
                .relatedTaskId(n.getRelatedTaskId())
                .actionTaken(n.isActionTaken())
                .actionTakenAt(n.getActionTakenAt())
                .build();
    }

    private Estudiante getEstudianteFromToken(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return estudianteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
    }
}