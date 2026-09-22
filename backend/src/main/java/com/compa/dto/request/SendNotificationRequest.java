package com.compa.dto.request;

import com.compa.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendNotificationRequest {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "El mensaje es obligatorio")
    private String message;

    // Opcional: si no se especifica, se envía solo in-app
    private NotificationChannel channel = NotificationChannel.IN_APP;

    // Opcional: si se indica, la notificación queda accionable ("Marcar cumplida") — HU-03
    private Long taskId;
}