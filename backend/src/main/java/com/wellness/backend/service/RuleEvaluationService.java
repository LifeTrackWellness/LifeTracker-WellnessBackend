package com.wellness.backend.service;

import com.wellness.backend.enums.PlanStatus;
import com.wellness.backend.model.*;
import com.wellness.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEvaluationService {
    private final PlanRuleRepository planRuleRepository;
    private final RuleEvaluationLogRepository evaluationLogRepository;
    private final HabitPlanRepository habitPlanRepository;
    private final PatientRepository patientRepository;
    private final AdherenceSnapshotRepository snapshotRepository;

    // Evalúa todas las reglas activas del plan activo del paciente
    @Transactional
    public void evaluateRulesForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null)
            return;

        // Solo evaluar planes activos
        HabitPlan activePlan = habitPlanRepository
                .findByPatientIdAndStatus(patientId, PlanStatus.ACTIVO)
                .orElse(null);
        if (activePlan == null) {
            log.info("Paciente {} no tiene plan activo — sin evaluación de reglas", patientId);
            return;
        }

        // Obtener reglas activas del plan
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

        // Usar métricas de adherencia calculadas por AdherenceService
        AdherenceSnapshot snapshot = snapshotRepository
                .findByPatientAndSnapshotDate(patient, today)
                .orElse(null);

        if (snapshot == null) {
            log.info("No hay snapshot de adherencia para paciente {} hoy — sin evaluación", patientId);
            return;
        }

        // Usar cumplimiento semanal del snapshot de adherencia
        double weeklyCompliance = snapshot.getWeeklyCompliance();

        for (PlanRule rule : activeRules) {
            // Verificar que no se haya evaluado hoy
            boolean alreadyEvaluated = evaluationLogRepository
                    .existsByPlanRuleIdAndPatientIdAndEvaluationDate(
                            rule.getId(), patientId, today);

            if (alreadyEvaluated) {
                log.info("Regla {} ya evaluada hoy para paciente {}", rule.getId(), patientId);
                continue;
            }

            // Evaluar — se dispara si la adherencia semanal es menor al umbral
            int umbral = rule.getUmbralPersonalizado() != null
                    ? rule.getUmbralPersonalizado()
                    : rule.getRuleTemplate().getUmbralDefault();

            boolean triggered = weeklyCompliance < umbral;

            // Registrar en log
            RuleEvaluationLog evalLog = new RuleEvaluationLog();
            evalLog.setPlanRule(rule);
            evalLog.setPatient(patient);
            evalLog.setEvaluationDate(today);
            evalLog.setTriggered(triggered);
            evalLog.setComplianceValue(weeklyCompliance);
            evaluationLogRepository.save(evalLog);

            if (triggered) {
                log.warn("🔴 Regla DISPARADA — Paciente: {}, Regla: '{}', Adherencia semanal: {}% < Umbral: {}%",
                        patientId,
                        rule.getRuleTemplate().getName(),
                        weeklyCompliance,
                        umbral);
            } else {
                log.info("✅ Regla NO disparada — Paciente: {}, Regla: '{}', Adherencia semanal: {}% >= Umbral: {}%",
                        patientId,
                        rule.getRuleTemplate().getName(),
                        weeklyCompliance,
                        umbral);
            }
        }
    }

}
