package com.wellness.backend.dto.response;

import com.wellness.backend.enums.AlertStatus;
import com.wellness.backend.enums.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AlertResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private AlertType type;
    private AlertStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
