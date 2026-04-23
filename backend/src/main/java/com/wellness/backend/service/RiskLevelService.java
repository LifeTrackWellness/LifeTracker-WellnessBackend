package com.wellness.backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wellness.backend.dto.response.RiskLevelHistoryResponse;
import com.wellness.backend.dto.response.RiskLevelResponse;
import com.wellness.backend.enums.PatientStatus;
import com.wellness.backend.enums.PlanStatus;
import com.wellness.backend.enums.RiskLevel;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.DailyCheckIn;
import com.wellness.backend.model.Patient;
import com.wellness.backend.model.RiskLevelHistory;
import com.wellness.backend.model.TaskCheckIn;
import com.wellness.backend.repository.DailyCheckInRepository;
import com.wellness.backend.repository.HabitPlanRepository;
import com.wellness.backend.repository.HabitTaskRepository;
import com.wellness.backend.repository.PatientRepository;
import com.wellness.backend.repository.RiskLevelHistoryRepository;
import com.wellness.backend.repository.TaskCheckInRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiskLevelService {
    private final PatientRepository patientRepository;
    private final HabitPlanRepository habitPlanRepository;
    private final HabitTaskRepository habitTaskRepository;
    private final DailyCheckInRepository checkInRepository;
    private final TaskCheckInRepository taskCheckInRepository;
    private final RiskLevelHistoryRepository riskLevelHistoryRepository;

    public RiskLevelService(PatientRepository patientRepository,
            HabitPlanRepository habitPlanRepository,
            HabitTaskRepository habitTaskRepository,
            DailyCheckInRepository checkInRepository,
            TaskCheckInRepository taskCheckInRepository,
            RiskLevelHistoryRepository riskLevelHistoryRepository) {
        this.patientRepository = patientRepository;
        this.habitPlanRepository = habitPlanRepository;
        this.habitTaskRepository = habitTaskRepository;
        this.checkInRepository = checkInRepository;
        this.taskCheckInRepository = taskCheckInRepository;
        this.riskLevelHistoryRepository = riskLevelHistoryRepository;
    }

    // Calcular y guardar nivel de riesgo de un paciente específico
    @Transactional
    public RiskLevelResponse evaluatePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        double compliance = calculateWeeklyCompliance(patientId);
        RiskLevel riskLevel = determineRiskLevel(compliance);

        RiskLevel previousRiskLevel = riskLevelHistoryRepository
                .findTopByPatientIdOrderByEvaluatedDateDesc(patientId)
                .map(RiskLevelHistory::getRiskLevel)
                .orElse(null);

        LocalDate today = LocalDate.now();

        // Si ya fue evaluado hoy, actualiza el registro
        RiskLevelHistory history = riskLevelHistoryRepository
                .findByPatientIdAndEvaluatedDate(patientId, today)
                .orElse(new RiskLevelHistory());

        history.setPatient(patient);
        history.setRiskLevel(riskLevel);
        history.setPreviousRiskLevel(previousRiskLevel);
        history.setCompliancePercentage(compliance);
        history.setEvaluatedDate(today);
        riskLevelHistoryRepository.save(history);

        return toResponse(patient, history);
    }

    // Obtener nivel de riesgo actual de un paciente
    @Transactional(readOnly = true)
    public RiskLevelResponse getCurrentRiskLevel(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        return riskLevelHistoryRepository
                .findTopByPatientIdOrderByEvaluatedDateDesc(patientId)
                .map(h -> toResponse(patient, h))
                .orElseGet(() -> {
                    // Si no hay historial, calcula en tiempo real sin guardar
                    double compliance = calculateWeeklyCompliance(patientId);
                    RiskLevel riskLevel = determineRiskLevel(compliance);
                    RiskLevelHistory temp = new RiskLevelHistory();
                    temp.setRiskLevel(riskLevel);
                    temp.setCompliancePercentage(compliance);
                    temp.setEvaluatedDate(LocalDate.now());
                    return toResponse(patient, temp);
                });
    }

    // Obtener historial de niveles de riesgo de un paciente
    @Transactional(readOnly = true)
    public List<RiskLevelHistoryResponse> getRiskLevelHistory(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        return riskLevelHistoryRepository
                .findByPatientIdOrderByEvaluatedDateDesc(patientId)
                .stream()
                .map(this::toHistoryResponse)
                .collect(Collectors.toList());
    }

    // Evaluar todos los pacientes activos (para ejecutar diariamente)
    @Transactional
    public List<RiskLevelResponse> evaluateAllActivePatients() {
        List<Patient> activePatients = patientRepository.findByStatus(PatientStatus.ACTIVO);
        List<RiskLevelResponse> results = new ArrayList<>();
        for (Patient patient : activePatients) {
            results.add(evaluatePatient(patient.getId()));
        }
        return results;
    }

    // Obtener todos los pacientes activos con su nivel de riesgo actual
    @Transactional(readOnly = true)
    public List<RiskLevelResponse> getAllPatientsRiskLevel() {
        List<Patient> activePatients = patientRepository.findByStatus(PatientStatus.ACTIVO);
        return activePatients.stream()
                .map(p -> getCurrentRiskLevel(p.getId()))
                .collect(Collectors.toList());
    }

    // --- Lógica de cálculo ---

    private double calculateWeeklyCompliance(Long patientId) {
        // Obtener plan activo
        var activePlan = habitPlanRepository
                .findByPatientIdAndStatus(patientId, PlanStatus.ACTIVO)
                .orElse(null);

        if (activePlan == null)
            return 0.0;

        // Tareas del plan
        int totalTasks = habitTaskRepository.findByHabitPlanId(activePlan.getId()).size();
        if (totalTasks == 0)
            return 0.0;

        // Check-ins de los últimos 7 días
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<DailyCheckIn> checkIns = checkInRepository
                .findByPatientIdOrderByCheckInDateDesc(patientId)
                .stream()
                .filter(c -> !c.getCheckInDate().isBefore(weekAgo) && !c.getCheckInDate().isAfter(today))
                .collect(Collectors.toList());

        if (checkIns.isEmpty())
            return 0.0;

        // Total de tareas completadas en la semana
        int totalExpected = totalTasks * 7;
        int totalCompleted = 0;

        for (DailyCheckIn checkIn : checkIns) {
            List<TaskCheckIn> taskCheckIns = taskCheckInRepository.findByCheckInId(checkIn.getId());
            totalCompleted += taskCheckIns.stream().filter(TaskCheckIn::isCompleted).count();
        }

        return Math.round((totalCompleted * 100.0 / totalExpected) * 10.0) / 10.0;
    }

    private RiskLevel determineRiskLevel(double compliance) {
        if (compliance >= 80)
            return RiskLevel.VERDE;
        if (compliance >= 50)
            return RiskLevel.AMARILLO;
        return RiskLevel.ROJO;
    }

    // --- Mappers ---

    private RiskLevelResponse toResponse(Patient patient, RiskLevelHistory history) {
        return RiskLevelResponse.builder()
                .patientId(patient.getId())
                .patientName(patient.getName() + " " + patient.getLastName())
                .riskLevel(history.getRiskLevel())
                .riskLevelDisplay(history.getRiskLevel().getDisplayName())
                .riskLevelDescription(history.getRiskLevel().getDescription())
                .compliancePercentage(history.getCompliancePercentage())
                .evaluatedDate(history.getEvaluatedDate())
                .createdAt(history.getCreatedAt())
                .build();
    }

    private RiskLevelHistoryResponse toHistoryResponse(RiskLevelHistory h) {
        return RiskLevelHistoryResponse.builder()
                .id(h.getId())
                .riskLevel(h.getRiskLevel())
                .riskLevelDisplay(h.getRiskLevel().getDisplayName())
                .previousRiskLevel(h.getPreviousRiskLevel())
                .previousRiskLevelDisplay(h.getPreviousRiskLevel() != null
                        ? h.getPreviousRiskLevel().getDisplayName()
                        : null)
                .compliancePercentage(h.getCompliancePercentage())
                .evaluatedDate(h.getEvaluatedDate())
                .createdAt(h.getCreatedAt())
                .build();
    }

}
