package com.wellness.backend.service;

import com.wellness.backend.dto.request.DailyCheckInRequest;
import com.wellness.backend.dto.request.TaskCheckInRequest;
import com.wellness.backend.enums.PlanStatus;
import com.wellness.backend.exception.BusinessException;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.*;
import com.wellness.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DailyCheckInService {

    private final DailyCheckInRepository checkInRepository;
    private final TaskCheckInRepository taskCheckInRepository;
    private final PatientRepository patientRepository;
    private final HabitPlanRepository habitPlanRepository;
    private final HabitTaskRepository habitTaskRepository;

    public DailyCheckInService(DailyCheckInRepository checkInRepository,
            TaskCheckInRepository taskCheckInRepository,
            PatientRepository patientRepository,
            HabitPlanRepository habitPlanRepository,
            HabitTaskRepository habitTaskRepository) {
        this.checkInRepository = checkInRepository;
        this.taskCheckInRepository = taskCheckInRepository;
        this.patientRepository = patientRepository;
        this.habitPlanRepository = habitPlanRepository;
        this.habitTaskRepository = habitTaskRepository;
    }

    // Paso 1: Obtener estado emocional disponibles
    public List<com.wellness.backend.enums.EmotionalState> getEmotionalStates() {
        return List.of(com.wellness.backend.enums.EmotionalState.values());
    }

    // Paso 2: Obtener tareas del día (del plan activo)
    @Transactional(readOnly = true)
    public List<HabitTask> getTodayTasks(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        HabitPlan activePlan = habitPlanRepository
                .findByPatientIdAndStatus(patientId, PlanStatus.ACTIVO)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no tiene un plan activo"));

        return habitTaskRepository.findByHabitPlanId(activePlan.getId());
    }

    // Crear check-in del día
    @Transactional
    public DailyCheckIn createCheckIn(Long patientId, DailyCheckInRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        LocalDate today = LocalDate.now();

        // Solo se puede hacer check-in una vez por día
        if (checkInRepository.existsByPatientIdAndCheckInDate(patientId, today)) {
            throw new BusinessException("Ya realizaste tu registro de hoy. Puedes editarlo hasta las 23:59.");
        }

        DailyCheckIn checkIn = new DailyCheckIn();
        checkIn.setPatient(patient);
        checkIn.setEmotionalState(request.getEmotionalState());
        checkIn.setCheckInDate(today);
        checkIn = checkInRepository.save(checkIn);

        // Guardar check-in de cada tarea
        if (request.getTasks() != null) {
            for (TaskCheckInRequest taskRequest : request.getTasks()) {
                HabitTask task = habitTaskRepository.findById(taskRequest.getTaskId())
                        .orElseThrow(() -> new ResourceNotFoundException("Tarea", taskRequest.getTaskId()));

                TaskCheckIn taskCheckIn = new TaskCheckIn();
                taskCheckIn.setCheckIn(checkIn);
                taskCheckIn.setTask(task);
                taskCheckIn.setCompleted(taskRequest.isCompleted());
                taskCheckIn.setBarrier(taskRequest.getBarrier());
                taskCheckInRepository.save(taskCheckIn);
            }
        }

        return checkInRepository.findById(checkIn.getId()).orElse(checkIn);
    }

    // Editar check-in del día (hasta las 23:59)
    @Transactional
    public DailyCheckIn updateCheckIn(Long patientId, DailyCheckInRequest request) {
        LocalDate today = LocalDate.now();

        DailyCheckIn checkIn = checkInRepository
                .findByPatientIdAndCheckInDate(patientId, today)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el registro de hoy"));

        // Verificar que sea antes de las 23:59
        LocalDateTime limit = today.atTime(23, 59);
        if (LocalDateTime.now().isAfter(limit)) {
            throw new BusinessException("Ya no puedes editar el registro de hoy.");
        }

        checkIn.setEmotionalState(request.getEmotionalState());
        checkIn = checkInRepository.save(checkIn);

        // Eliminar tasks anteriores y recrear
        List<TaskCheckIn> existing = taskCheckInRepository.findByCheckInId(checkIn.getId());
        taskCheckInRepository.deleteAll(existing);

        if (request.getTasks() != null) {
            for (TaskCheckInRequest taskRequest : request.getTasks()) {
                HabitTask task = habitTaskRepository.findById(taskRequest.getTaskId())
                        .orElseThrow(() -> new ResourceNotFoundException("Tarea", taskRequest.getTaskId()));

                TaskCheckIn taskCheckIn = new TaskCheckIn();
                taskCheckIn.setCheckIn(checkIn);
                taskCheckIn.setTask(task);
                taskCheckIn.setCompleted(taskRequest.isCompleted());
                taskCheckIn.setBarrier(taskRequest.getBarrier());
                taskCheckInRepository.save(taskCheckIn);
            }
        }

        return checkInRepository.findById(checkIn.getId()).orElse(checkIn);
    }

    // Obtener check-in de hoy
    @Transactional(readOnly = true)
    public DailyCheckIn getTodayCheckIn(Long patientId) {
        return checkInRepository
                .findByPatientIdAndCheckInDate(patientId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el registro de hoy"));
    }

    // Obtener racha actual del paciente
    @Transactional(readOnly = true)
    public int getCurrentStreak(Long patientId) {
        List<DailyCheckIn> checkIns = checkInRepository
                .findByPatientIdOrderByCheckInDateDesc(patientId);

        if (checkIns.isEmpty())
            return 0;

        int streak = 0;
        LocalDate expected = LocalDate.now();

        for (DailyCheckIn checkIn : checkIns) {
            if (checkIn.getCheckInDate().equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    // Mensaje contextual basado en la racha (Paso 3)
    public String getClosingMessage(Long patientId) {
        int streak = getCurrentStreak(patientId);

        if (streak == 1)
            return "Primer dia completado. Cada gran habito empieza con un primer paso.";
        if (streak < 4)
            return "Llevas " + streak + " dias seguidos. Lo estas logrando!";
        if (streak < 7)
            return "Increible! " + streak + " dias de racha. Estas construyendo un habito real.";
        if (streak < 14)
            return "Una semana completa! " + streak + " dias seguidos. Eres constante.";
        return "Llevas " + streak + " dias consecutivos. Eres una inspiracion para ti mismo.";
    }

    // Historial completo de check-ins
    @Transactional(readOnly = true)
    public List<DailyCheckIn> getHistory(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));
        return checkInRepository.findByPatientIdOrderByCheckInDateDesc(patientId);
    }
}