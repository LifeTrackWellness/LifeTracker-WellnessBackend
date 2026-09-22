package com.wellness.backend.dto.request;

import com.wellness.backend.enums.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Data;

@Data
public class GuardianRequest {
    @NotBlank(message = "El nombre del acudiente es obligatorio")
    private String name;

    @NotBlank(message = "Los apellidos del acudiente son obligatorios")
    private String lastName;

    @NotNull(message = "El tipo de documento del acudiente es obligatorio")
    private DocumentType documentType;

    @NotBlank(message = "El documento del acudiente es obligatorio")
    private String identityDocument;

    @NotBlank(message = "El parentesco es obligatorio")
    private String relationship;

    @Email(message = "Debe ser un correo válido")
    private String email;

    private String phoneNumber;

}
