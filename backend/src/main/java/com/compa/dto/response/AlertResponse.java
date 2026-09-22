package com.compa.dto.response;

import com.compa.enums.AlertStatus;
import com.compa.enums.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AlertResponse {
    private Long id;
    private Long estudianteId;
    private String estudianteName;
    private AlertType type;
    private AlertStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
