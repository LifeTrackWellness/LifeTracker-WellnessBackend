package com.wellness.backend.service;

import com.wellness.backend.dto.request.HabitPlanRequest;
import com.wellness.backend.dto.request.HabitTaskRequest;
import com.wellness.backend.enums.PlanStatus;
import com.wellness.backend.exception.BusinessException;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.HabitPlan;
import com.wellness.backend.model.HabitTask;
import com.wellness.backend.model.Patient;
import com.wellness.backend.repository.HabitPlanRepository;
import com.wellness.backend.repository.HabitTaskRepository;
import com.wellness.backend.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class HabitPlanService {

    private final HabitPlanRepository habitPlanRepository;
    private final HabitTaskRepository habitTaskRepository;
    private final PatientRepository patientRepository;

    public HabitPlanService(HabitPlanRepository habitPlanRepository, HabitTaskRepository habitTaskRepository,
            PatientRepository patientRepository) {
        this.habitPlanRepository = habitPlanRepository;
        this.habitTaskRepository = habitTaskRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public HabitPlan createPlan(Long patientId, HabitPlanRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        if (habitPlanRepository.existsByPatientIdAndStatus(patientId, PlanStatus.ACTIVO)) {
            throw new BusinessException(
                    "El paciente ya tiene un plan activo. Desactiva el plan actual antes de crear uno nuevo.");
        }

        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }

        HabitPlan plan = new HabitPlan();
        plan.setPatient(patient);
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setStatus(PlanStatus.ACTIVO);
        plan = habitPlanRepository.save(plan);

        if (request.getTasks() != null) {
            for (HabitTaskRequest taskRequest : request.getTasks()) {
                HabitTask task = new HabitTask();
                task.setHabitPlan(plan);
                task.setName(taskRequest.getName());
                task.setDescription(taskRequest.getDescription());

                // HU10//
                task.setPriority(taskRequest.getPriority());
                task.setMandatory(taskRequest.isMandatory());
                task.setWeeklyGoal(taskRequest.getWeeklyGoal());
                task.setSpecificDays(taskRequest.getSpecificDays());

                habitTaskRepository.save(task);
            }
        }
        HabitPlan savedPlan = habitPlanRepository.findById(plan.getId()).orElse(plan);
        savedPlan.getTasks().size(); // fuerza la carga de las tareas
        return savedPlan;
    }

    @Transactional(readOnly = true)
    public List<HabitPlan> getPlansByPatient(Long patientId) {
        patientRepository.findById(patientId).orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));
        return habitPlanRepository.findByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public HabitPlan getActivePlan(Long patientId) {
        patientRepository.findById(patientId).orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));
        return habitPlanRepository.findByPatientIdAndStatus(patientId, PlanStatus.ACTIVO)
                .orElseThrow(() -> new ResourceNotFoundException("El paciente no tiene un plan activo"));
    }

    @Transactional(readOnly = true)
    public HabitPlan getPlanById(Long planId) {
        return habitPlanRepository.findById(planId).orElseThrow(() -> new ResourceNotFoundException("Plan", planId));
    }

    @Transactional
    public HabitPlan updatePlan(Long planId, HabitPlanRequest request) {
        HabitPlan plan = habitPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", planId));
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        return habitPlanRepository.save(plan);
    }

    @Transactional
    public HabitPlan deactivatePlan(Long planId) {
        HabitPlan plan = habitPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", planId));
        if (PlanStatus.INACTIVO.equals(plan.getStatus())) {
            throw new BusinessException("El plan ya se encuentra inactivo.");
        }
        plan.setStatus(PlanStatus.INACTIVO);
        return habitPlanRepository.save(plan);
    }

    @Transactional
    public HabitTask addTask(Long planId, HabitTaskRequest request) {
        HabitPlan plan = habitPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", planId));

        HabitTask task = new HabitTask();
        task.setHabitPlan(plan);
        task.setName(request.getName());
        task.setDescription(request.getDescription());

        // -DE LA HU-10 ---
        task.setPriority(request.getPriority());        // Criterio: Prioridad
        task.setMandatory(request.isMandatory());       // Criterio: Obligatoriedad
        task.setWeeklyGoal(request.getWeeklyGoal());    // Criterio: Objetivo semanal
        task.setSpecificDays(request.getSpecificDays());// Criterio: Frecuencia

        return habitTaskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        HabitTask task = habitTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", taskId));
        habitTaskRepository.delete(task);
    }
    @Transactional
    public HabitTask updateTask(Long taskId, HabitTaskRequest request) {
        HabitTask task = habitTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", taskId));

        // Solo se permite editar si el plan está activo
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setMandatory(request.isMandatory());
        task.setWeeklyGoal(request.getWeeklyGoal());
        task.setSpecificDays(request.getSpecificDays());

        return habitTaskRepository.save(task);
    }
}