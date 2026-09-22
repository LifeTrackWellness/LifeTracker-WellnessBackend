package com.compa.service;

import com.compa.enums.AlertType;
import com.compa.enums.PlanStatus;
import com.compa.model.*;
import com.compa.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class RuleEvaluationService {

    private final PlanRuleRepository planRuleRepository;
    private final RuleEvaluationLogRepository evaluationLogRepository;
    private final HabitPlanRepository habitPlanRepository;
    private final EstudianteRepository estudianteRepository;
    private final AdherenceSnapshotRepository snapshotRepository;
    private final AlertService alertService;

    public RuleEvaluationService(
            PlanRuleRepository planRuleRepository,
            RuleEvaluationLogRepository evaluationLogRepository,
            HabitPlanRepository habitPlanRepository,
            EstudianteRepository estudianteRepository,
            AdherenceSnapshotRepository snapshotRepository,
            @Lazy AlertService alertService) {
        this.planRuleRepository = planRuleRepository;
        this.evaluationLogRepository = evaluationLogRepository;
        this.habitPlanRepository = habitPlanRepository;
        this.estudianteRepository = estudianteRepository;
        this.snapshotRepository = snapshotRepository;
        this.alertService = alertService;
    }

    @Transactional
    public void evaluateRulesForEstudiante(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
        if (estudiante == null) return;

        HabitPlan activePlan = habitPlanRepository
                .findByEstudianteIdAndStatus(estudianteId, PlanStatus.ACTIVO)
                .orElse(null);
        if (activePlan == null) {
            log.info("Estudiante {} no tiene plan activo — sin evaluación de reglas", estudianteId);
            return;
        }

        List<PlanRule> activeRules = planRuleRepository
                .findAllByHabitPlan(activePlan)
                .stream()
                .filter(PlanRule::isActive)
                .toList();

        if (activeRules.isEmpty()) {
            log.info("Plan {} no tiene reglas activas", activePlan.getId());
            return;
        }

        LocalDate today = LocalDate.now(ZoneId.of("America/Bogota"));

        AdherenceSnapshot snapshot = snapshotRepository
                .findByEstudianteAndSnapshotDate(estudiante, today)
                .orElse(null);

        if (snapshot == null) {
            log.info("No hay snapshot de adherencia para estudiante {} hoy — sin evaluación", estudianteId);
            return;
        }

        double weeklyCompliance = snapshot.getWeeklyCompliance();

        for (PlanRule rule : activeRules) {
            boolean alreadyEvaluated = evaluationLogRepository
                    .existsByPlanRuleIdAndEstudianteIdAndEvaluationDate(
                            rule.getId(), estudianteId, today);

            if (alreadyEvaluated) {
                log.info("Regla {} ya evaluada hoy para estudiante {}", rule.getId(), estudianteId);
                continue;
            }

            int umbral = rule.getUmbralPersonalizado() != null
                    ? rule.getUmbralPersonalizado()
                    : rule.getRuleTemplate().getUmbralDefault();

            boolean triggered = weeklyCompliance < umbral;

            RuleEvaluationLog evalLog = new RuleEvaluationLog();
            evalLog.setPlanRule(rule);
            evalLog.setEstudiante(estudiante);
            evalLog.setEvaluationDate(today);
            evalLog.setTriggered(triggered);
            evalLog.setComplianceValue(weeklyCompliance);
            evaluationLogRepository.save(evalLog);

            if (triggered) {
                log.warn("🔴 Regla DISPARADA — Estudiante: {}, Regla: '{}', Adherencia: {}% < Umbral: {}%",
                        estudianteId, rule.getRuleTemplate().getName(), weeklyCompliance, umbral);

                alertService.createAlertIfNotExists(
                        estudianteId,
                        AlertType.RIESGO_ALTO,
                        "Regla disparada: '" + rule.getRuleTemplate().getName() +
                                "' — Adherencia semanal: " + weeklyCompliance + "%"
                );
            } else {
                log.info("✅ Regla NO disparada — Estudiante: {}, Regla: '{}', Adherencia: {}%",
                        estudianteId, rule.getRuleTemplate().getName(), weeklyCompliance);
            }
        }
    }
}
