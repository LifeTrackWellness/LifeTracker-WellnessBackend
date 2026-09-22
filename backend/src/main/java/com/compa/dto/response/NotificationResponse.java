package com.compa.dto.response;

import com.compa.enums.NotificationChannel;
import com.compa.enums.NotificationStatus;
import com.compa.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long estudianteId;
    private String estudianteName;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    // HU-03: permiten al frontend mostrar el botón "Marcar cumplida" y su confirmación visual
    private Long relatedTaskId;
    private boolean actionTaken;
    private LocalDateTime actionTakenAt;
}