package com.wellness.backend.dto.response;

import com.wellness.backend.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardianResponse {
    private Long id;
    private String name;
    private String lastName;
    private DocumentType documentType;
    private String identityDocument;
    private String relationship;
    private String email;
    private String phoneNumber;
    private Long patientId;

}
