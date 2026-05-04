package com.wellness.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConclusionRequest
{
    @NotBlank(message = "La conclusión no puede estar vacía")
    private String content;

    private Long professionalId;
}
