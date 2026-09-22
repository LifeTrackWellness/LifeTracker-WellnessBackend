package com.compa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.compa.enums.NotificationChannel;
import com.compa.enums.NotificationStatus;
import com.compa.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDIENTE;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // --- Soporte para acción rápida "Marcar tarea cumplida" (HU-03) ---

    @Column(name = "related_task_id")
    private Long relatedTaskId;

    @Column(name = "action_token", unique = true)
    private String actionToken;

    @Column(name = "action_token_expires_at")
    private LocalDateTime actionTokenExpiresAt;

    @Column(name = "action_taken", nullable = false)
    @Builder.Default
    private boolean actionTaken = false;

    @Column(name = "action_taken_at")
    private LocalDateTime actionTakenAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = NotificationStatus.PENDIENTE;
        }
    }
}
