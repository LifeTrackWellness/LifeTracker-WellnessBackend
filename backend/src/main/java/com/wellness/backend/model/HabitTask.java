package com.wellness.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "habit_tasks")
@Data
public class HabitTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    // Criterio: Prioridad (alta/media/baja)
    private String priority;

    // Criterio: Obligatoria u opcional
    private boolean mandatory;

    // Criterio: Objetivo semanal (ej: 4 veces)
    private Integer weeklyGoal;

    // Criterio: Frecuencia (días específicos)
    @ElementCollection
    private List<String> specificDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    @JsonIgnore // Evita errores de recursión infinita
    private HabitPlan habitPlan;
}