package com.wellness.backend.dto.request;

import com.wellness.backend.enums.EmotionalState;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DailyCheckInRequest {

    @NotNull(message = "El estado emocional es obligatorio")
    private EmotionalState emotionalState;

    private List<TaskCheckInRequest> tasks = new ArrayList<>();
}