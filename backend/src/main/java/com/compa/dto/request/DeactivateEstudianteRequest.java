package com.wellness.backend.dto.request;

import com.wellness.backend.enums.DeactivationReason;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeactivatePatientRequest {
    @NotNull(message = "El motivo de desactivacion es obligatorio")
    private DeactivationReason reason;

}
