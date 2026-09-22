package com.wellness.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "patient_consent")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PatientConsent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnore
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "consent_template_id", nullable = false)
    private ConsentTemplate consentTemplate;

    @Column(nullable = false)
    private boolean aceptado = false;

    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion;

}
