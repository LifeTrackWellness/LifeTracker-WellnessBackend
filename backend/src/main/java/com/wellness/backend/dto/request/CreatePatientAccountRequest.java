package com.wellness.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreatePatientAccountRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @Email(message = "Debe ser un correo válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "El documento es obligatorio")
    private String identityDocument;

    @Pattern(regexp = "CEDULA|TARJETA_DE_IDENTIDAD", message = "Tipo de documento inválido")
    private String documentType = "CEDULA";

    private String phoneNumber;
}