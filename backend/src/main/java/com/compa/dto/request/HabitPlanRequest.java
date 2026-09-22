package com.wellness.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class HabitPlanRequest {
    @NotBlank(message = "El nombre del plan es obligatorio")
    private String name;
    private String description;
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;
    private LocalDate endDate;
    private List<HabitTaskRequest> tasks = new ArrayList<>();
}