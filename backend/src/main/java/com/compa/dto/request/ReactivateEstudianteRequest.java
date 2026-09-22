package com.compa.dto.request;

import com.compa.enums.DocumentType;
import com.compa.enums.HealthStatus;
import lombok.Data;

@Data
public class ReactivateEstudianteRequest {
    private String name;
    private String lastName;
    private DocumentType documentType;
    private String phoneNumber;
    private String email;
    private String mainCondition;
    private String secondaryConditions;
    private HealthStatus healthStatus;
    private String justification;
}
