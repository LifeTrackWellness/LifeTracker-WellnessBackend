package com.compa.dto.request;

import com.compa.enums.DeactivationReason;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeactivateEstudianteRequest {
    @NotNull(message = "El motivo de desactivacion es obligatorio")
    private DeactivationReason reason;

}
