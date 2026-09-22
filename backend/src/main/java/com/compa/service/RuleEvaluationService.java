package com.wellness.backend.service;

import com.wellness.backend.enums.AlertType;
import com.wellness.backend.enums.PlanStatus;
import com.wellness.backend.model.*;
import com.wellness.backend.repository.*;
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
    private final PatientRepository patientRepository;
    private final AdherenceSnapshotRepository snapshotRepository;
    private final AlertService alertService;

    public RuleEvaluationService(
            PlanRuleRepository planRuleRepository,
            RuleEvaluationLogRepository evaluationLogRepository,
            HabitPlanRepository habitPlanRepository,
            PatientRepository patientRepository,
            AdherenceSnapshotRepository snapshotRepository,
            @Lazy AlertService alertService) {
        this.planRuleRepository = planRuleRepository;
        this.evaluationLogRepository = evaluationLogRepository;
        this.habitPlanRepository = habitPlanRepository;
        this.patientRepository = patientRepository;
        this.snapshotRepository = snapshotRepository;
        this.alertService = alertService;
    }

    @Transactional
    public void evaluateRulesForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) return;

        HabitPlan activePlan = habitPlanRepository
                .findByPatientIdAndStatus(patientId, PlanStatus.ACTIVO)
                .orElse(null);
        if (activePlan == null) {
            log.info("Paciente {} no tiene plan activo — sin evaluación de reglas", patientId);
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
                .findByPatientAndSnapshotDate(patient, today)
                .orElse(null);

        if (snapshot == null) {
            log.info("No hay snapshot de adherencia para paciente {} hoy — sin evaluación", patientId);
            return;
        }

        double weeklyCompliance = snapshot.getWeeklyCompliance();

        for (PlanRule rule : activeRules) {
            boolean alreadyEvaluated = evaluationLogRepository
                    .existsByPlanRuleIdAndPatientIdAndEvaluationDate(
                            rule.getId(), patientId, today);

            if (alreadyEvaluated) {
                log.info("Regla {} ya evaluada hoy para paciente {}", rule.getId(), patientId);
                continue;
            }

            int umbral = rule.getUmbralPersonalizado() != null
                    ? rule.getUmbralPersonalizado()
                    : rule.getRuleTemplate().getUmbralDefault();

            boolean triggered = weeklyCompliance < umbral;

            RuleEvaluationLog evalLog = new RuleEvaluationLog();
            evalLog.setPlanRule(rule);
            evalLog.setPatient(patient);
            evalLog.setEvaluationDate(today);
            evalLog.setTriggered(triggered);
            evalLog.setComplianceValue(weeklyCompliance);
            evaluationLogRepository.save(evalLog);

            if (triggered) {
                log.warn("🔴 Regla DISPARADA — Paciente: {}, Regla: '{}', Adherencia: {}% < Umbral: {}%",
                        patientId, rule.getRuleTemplate().getName(), weeklyCompliance, umbral);

                alertService.createAlertIfNotExists(
                        patientId,
                        AlertType.RIESGO_ALTO,
                        "Regla disparada: '" + rule.getRuleTemplate().getName() +
                                "' — Adherencia semanal: " + weeklyCompliance + "%"
                );
            } else {
                log.info("✅ Regla NO disparada — Paciente: {}, Regla: '{}', Adherencia: {}%",
                        patientId, rule.getRuleTemplate().getName(), weeklyCompliance);
            }
        }
    }
}
