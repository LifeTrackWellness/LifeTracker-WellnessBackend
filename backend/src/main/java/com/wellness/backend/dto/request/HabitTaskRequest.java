package com.wellness.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class HabitTaskRequest {
    @NotBlank(message = "El nombre de la tarea es obligatorio")
    private String name;
    private String description;

    // Criterio: Prioridad (alta/media/baja)
    private String priority;

    // Criterio: Obligatoria u opcional
    private boolean mandatory;

    // Criterio: Objetivo semanal (ej: 4 veces por semana)
    private Integer weeklyGoal;

    // Criterio: Frecuencia (días específicos de la semana)
    private List<String> specificDays;
}