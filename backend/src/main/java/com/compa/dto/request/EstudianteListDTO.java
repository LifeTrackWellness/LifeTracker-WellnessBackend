package com.compa.dto.request;

import com.compa.enums.EstudianteStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstudianteListDTO {
    private String name;
    private String document;
    private String primaryCondition;
    private EstudianteStatus status;
}
