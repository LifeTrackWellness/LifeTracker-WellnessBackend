package com.compa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recordatorios_tarea")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordatorioTarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_task_id", nullable = false)
    private HabitTask habitTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "recordatorios_enviados", nullable = false)
    private int recordatoriosEnviados = 1;

    @Column(name = "primer_envio", nullable = false)
    private LocalDateTime primerEnvio;

    @Column(name = "segundo_envio")
    private LocalDateTime segundoEnvio;

    @PrePersist
    protected void onCreate() {
        if (this.primerEnvio == null) {
            this.primerEnvio = LocalDateTime.now();
        }
    }
}
