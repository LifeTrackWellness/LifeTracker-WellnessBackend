package com.compa.repository;

import com.compa.model.RecordatorioTarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordatorioTareaRepository extends JpaRepository<RecordatorioTarea, Long> {

    // Para saber si una tarea ya recibió recordatorio hoy (evitar duplicar el
    // primer envío)
    Optional<RecordatorioTarea> findByHabitTaskIdAndFecha(Long habitTaskId, LocalDate fecha);

    // Para el job de seguimiento: recordatorios que ya pasaron 24h y solo tienen 1
    // envío
    List<RecordatorioTarea> findByRecordatoriosEnviadosAndPrimerEnvioBefore(int recordatoriosEnviados,
            LocalDateTime limite);
}
