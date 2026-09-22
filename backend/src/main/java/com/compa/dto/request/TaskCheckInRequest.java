package com.compa.dto.request;

import com.compa.enums.TaskBarrier;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskCheckInRequest {
    @NotNull(message = "El id de la tarea es obligatorio")
    private Long taskId;

    @NotNull(message = "El estado de completado es obligatorio")
    private boolean completed;

    private TaskBarrier barrier;

}
