package com.wellness.backend.dto.request;

import com.wellness.backend.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HabitTaskRequest {
    @NotBlank(message = "El nombre de la tarea es obligatorio")
    private String name;
    private String description;
    private TaskPriority priority = TaskPriority.MEDIA;
    private boolean mandatory = false;
    private Integer weeklyGoal;
    private List<String> specificDays = new ArrayList<>();
}