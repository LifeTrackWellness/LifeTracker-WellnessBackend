package com.wellness.backend.service;

import com.wellness.backend.dto.request.ConclusionRequest;
import com.wellness.backend.dto.response.ProgressReportResponse;
import com.wellness.backend.enums.RiskLevel;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.*;
import com.wellness.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressReportService {

    private final PatientRepository patientRepository;
    private final ProfessionalRepository professionalRepository;
    private final AdherenceSnapshotRepository adherenceSnapshotRepository;
    private final RiskLevelHistoryRepository riskLevelHistoryRepository;
    private final HealthStatusHistoryRepository healthStatusHistoryRepository;
    private final HabitPlanRepository habitPlanRepository;
    private final TherapistConclusionRepository conclusionRepository;

    public ProgressReportResponse getProgressReport(Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        // 1. Métricas de adherencia
        AdherenceSnapshot latestSnapshot = adherenceSnapshotRepository
                .findByPatientOrderBySnapshotDateDesc(patient)
                .stream().findFirst().orElse(null);

        Double weeklyCompliance = latestSnapshot != null ? latestSnapshot.getWeeklyCompliance() : 0.0;
        Integer currentStreak = latestSnapshot != null ? latestSnapshot.getCurrentStreak() : 0;

        // Mejor racha histórica
        Integer bestStreak = adherenceSnapshotRepository
                .findByPatientOrderBySnapshotDateDesc(patient)
                .stream()
                .mapToInt(AdherenceSnapshot::getCurrentStreak)
                .max()
                .orElse(0);

        // 2. Últimos 7 días de riesgo
        List<RiskLevelHistory> riskHistory = riskLevelHistoryRepository
                .findTop7ByPatientOrderByEvaluatedDateDesc(patient);

        List<ProgressReportResponse.RiskDayDTO> last7DaysRisk = riskHistory.stream()
                .map(r -> ProgressReportResponse.RiskDayDTO.builder()
                        .date(r.getEvaluatedDate())
                        .riskLevel(r.getRiskLevel())
                        .compliancePercentage(r.getCompliancePercentage())
                        .build())
                .collect(Collectors.toList());

        // Veces en riesgo ROJO
        Long highRiskCount = riskLevelHistoryRepository
                .countByPatientAndRiskLevel(patient, RiskLevel.ROJO);

        // 3. Evolución de salud
        List<HealthStatusHistory> statusHistory = healthStatusHistoryRepository
                .findByClinicalInfo_PatientOrderByChangedAtAsc(patient);

        HealthStatusHistory initialStatus = statusHistory.isEmpty() ? null : statusHistory.get(0);
        HealthStatusHistory currentStatus = statusHistory.isEmpty() ? null
                : statusHistory.get(statusHistory.size() - 1);

        // 4. Planes de hábitos
        List<ProgressReportResponse.HabitPlanDTO> habitPlans = habitPlanRepository
                .findByPatient(patient)
                .stream()
                .map(p -> ProgressReportResponse.HabitPlanDTO.builder()
                        .name(p.getName())
                        .startDate(p.getStartDate())
                        .status(p.getStatus())
                        .build())
                .collect(Collectors.toList());

        // 5. Conclusiones
        List<ProgressReportResponse.ConclusionDTO> conclusions = conclusionRepository
                .findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(c -> ProgressReportResponse.ConclusionDTO.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .therapistName(c.getProfessional().getName()
                                + " " + c.getProfessional().getLastName())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ProgressReportResponse.builder()
                .patientFullName(patient.getName() + " " + patient.getLastName())
                .therapistFullName("LifeTracker Wellness")
                .generatedAt(LocalDateTime.now())
                .weeklyCompliance(weeklyCompliance)
                .currentStreak(currentStreak)
                .bestStreak(bestStreak)
                .last7DaysRisk(last7DaysRisk)
                .highRiskCount(highRiskCount)
                .initialHealthStatus(initialStatus != null ? initialStatus.getNewStatus() : null)
                .currentHealthStatus(currentStatus != null ? currentStatus.getNewStatus() : null)
                .initialStatusDate(initialStatus != null ? initialStatus.getChangedAt() : null)
                .currentStatusDate(currentStatus != null ? currentStatus.getChangedAt() : null)
                .habitPlans(habitPlans)
                .conclusions(conclusions)
                .build();
    }

    public TherapistConclusion addConclusion(Long patientId, ConclusionRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        Professional professional = professionalRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional", request.getProfessionalId()));

        TherapistConclusion conclusion = TherapistConclusion.builder()
                .patient(patient)
                .professional(professional)
                .content(request.getContent())
                .build();

        return conclusionRepository.save(conclusion);
    }
}