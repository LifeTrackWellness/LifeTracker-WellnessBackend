package com.wellness.backend.dto.request;

import com.wellness.backend.enums.HealthStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClinicalInfoRequest {
    @NotBlank(message = "La condicion principal es obligatoria")
    private String mainCondition;
    private String secondaryConditions;
    @NotNull(message = "El estado de salud es obligatorio")
    private HealthStatus healthStatus;
    @NotBlank(message = "La justificacion es obligatoria")
    private String justification;

}
