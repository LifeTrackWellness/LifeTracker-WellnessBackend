package com.compa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "estudiante_consent")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class EstudianteConsent
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    @JsonIgnore
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "consent_template_id", nullable = false)
    private ConsentTemplate consentTemplate;

    @Column(nullable = false)
    private boolean aceptado = false;

    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion;

}
