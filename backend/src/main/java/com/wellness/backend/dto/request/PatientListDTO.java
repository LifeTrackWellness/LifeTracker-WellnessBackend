package com.wellness.backend.dto.request;

import com.wellness.backend.enums.PatientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientListDTO {
    private String name;
    private String document;
    private String primaryCondition;
    private PatientStatus status;
}
