package com.compa.service;

import com.compa.dto.request.ConclusionRequest;
import com.compa.dto.response.ProgressReportResponse;
import com.compa.enums.RiskLevel;
import com.compa.exception.ResourceNotFoundException;
import com.compa.model.*;
import com.compa.repository.*;
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

    private final EstudianteRepository estudianteRepository;
    private final OrientadorRepository orientadorRepository;
    private final AdherenceSnapshotRepository adherenceSnapshotRepository;
    private final RiskLevelHistoryRepository riskLevelHistoryRepository;
    private final HealthStatusHistoryRepository healthStatusHistoryRepository;
    private final HabitPlanRepository habitPlanRepository;
    private final OrientadorNotaRepository conclusionRepository;

    public ProgressReportResponse getProgressReport(Long estudianteId) {

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", estudianteId));

        // 1. Métricas de adherencia
        AdherenceSnapshot latestSnapshot = adherenceSnapshotRepository
                .findByEstudianteOrderBySnapshotDateDesc(estudiante)
                .stream().findFirst().orElse(null);

        Double weeklyCompliance = latestSnapshot != null ? latestSnapshot.getWeeklyCompliance() : 0.0;
        Integer currentStreak = latestSnapshot != null ? latestSnapshot.getCurrentStreak() : 0;

        // Mejor racha histórica
        Integer bestStreak = adherenceSnapshotRepository
                .findByEstudianteOrderBySnapshotDateDesc(estudiante)
                .stream()
                .mapToInt(AdherenceSnapshot::getCurrentStreak)
                .max()
                .orElse(0);

        // 2. Últimos 7 días de riesgo
        List<RiskLevelHistory> riskHistory = riskLevelHistoryRepository
                .findTop7ByEstudianteOrderByEvaluatedDateDesc(estudiante);

        List<ProgressReportResponse.RiskDayDTO> last7DaysRisk = riskHistory.stream()
                .map(r -> ProgressReportResponse.RiskDayDTO.builder()
                        .date(r.getEvaluatedDate())
                        .riskLevel(r.getRiskLevel())
                        .compliancePercentage(r.getCompliancePercentage())
                        .build())
                .collect(Collectors.toList());

        // Veces en riesgo ROJO
        Long highRiskCount = riskLevelHistoryRepository
                .countByEstudianteAndRiskLevel(estudiante, RiskLevel.ROJO);

        // 3. Evolución de salud
        List<HealthStatusHistory> statusHistory = healthStatusHistoryRepository
                .findByClinicalInfo_EstudianteOrderByChangedAtAsc(estudiante);

        HealthStatusHistory initialStatus = statusHistory.isEmpty() ? null : statusHistory.get(0);
        HealthStatusHistory currentStatus = statusHistory.isEmpty() ? null
                : statusHistory.get(statusHistory.size() - 1);

        // 4. Planes de hábitos
        List<ProgressReportResponse.HabitPlanDTO> habitPlans = habitPlanRepository
                .findByEstudiante(estudiante)
                .stream()
                .map(p -> ProgressReportResponse.HabitPlanDTO.builder()
                        .name(p.getName())
                        .startDate(p.getStartDate())
                        .status(p.getStatus())
                        .build())
                .collect(Collectors.toList());

        // 5. Conclusiones
        List<ProgressReportResponse.ConclusionDTO> conclusions = conclusionRepository
                .findByEstudianteIdOrderByCreatedAtDesc(estudianteId)
                .stream()
                .map(c -> ProgressReportResponse.ConclusionDTO.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .orientadorName(c.getOrientador().getName()
                                + " " + c.getOrientador().getLastName())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ProgressReportResponse.builder()
                .estudianteFullName(estudiante.getName() + " " + estudiante.getLastName())
                .orientadorFullName("COMPA - Acompañamiento estudiantil")
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

    public OrientadorNota addConclusion(Long estudianteId, ConclusionRequest request) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", estudianteId));

        Orientador orientador = orientadorRepository.findById(request.getOrientadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Orientador", request.getOrientadorId()));

        OrientadorNota conclusion = OrientadorNota.builder()
                .estudiante(estudiante)
                .orientador(orientador)
                .content(request.getContent())
                .build();

        return conclusionRepository.save(conclusion);
    }
}