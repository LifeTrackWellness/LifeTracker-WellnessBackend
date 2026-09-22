package com.wellness.backend.dto.request;

import com.wellness.backend.enums.HealthStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HealthStatusUpdateRequest {
    @NotNull(message = "El nuevo estado es obligatorio")
    private HealthStatus newStatus;
    @NotBlank(message = "La justificacion es obligatoria")
    @Size(min = 10, max = 500, message = "La justificacion debe tener entre 10 y 500 caracteres")
    private String justification;
}