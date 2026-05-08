package com.wellness.backend.service;

import com.wellness.backend.enums.PlanStatus;
import com.wellness.backend.model.*;
import com.wellness.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEvaluationService {
    private final PlanRuleRepository planRuleRepository;
    private final RuleEvaluationLogRepository evaluationLogRepository;
    private final HabitPlanRepository habitPlanRepository;
    private final DailyCheckInRepository checkInRepository;
    private final TaskCheckInRepository taskCheckInRepository;
    private final PatientRepository patientRepository;

    // Evalúa todas las reglas activas del plan activo del paciente
    @Transactional
    public void evaluateRulesForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElse(null);
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

        // Calcular cumplimiento de hoy
        double todayCompliance = calculateTodayCompliance(patientId);
        LocalDate today = LocalDate.now();

        for (PlanRule rule : activeRules) {
            // Verificar que no se haya disparado hoy
            boolean alreadyEvaluated = evaluationLogRepository
                    .existsByPlanRuleIdAndPatientIdAndEvaluationDate(
                            rule.getId(), patientId, today);

            if (alreadyEvaluated) {
                log.info("Regla {} ya evaluada hoy para paciente {}", rule.getId(), patientId);
                continue;
            }

            // Evaluar la regla — se dispara si el cumplimiento es menor al umbral
            int umbral = rule.getUmbralPersonalizado() != null
                    ? rule.getUmbralPersonalizado()
                    : rule.getRuleTemplate().getUmbralDefault();

            boolean triggered = todayCompliance < umbral;

            // Registrar en log
            RuleEvaluationLog evalLog = new RuleEvaluationLog();
            evalLog.setPlanRule(rule);
            evalLog.setPatient(patient);
            evalLog.setEvaluationDate(today);
            evalLog.setTriggered(triggered);
            evalLog.setComplianceValue(todayCompliance);
            evaluationLogRepository.save(evalLog);

            if (triggered) {
                log.warn("🔴 Regla DISPARADA — Paciente: {}, Regla: '{}', Cumplimiento: {}% < Umbral: {}%",
                        patientId,
                        rule.getRuleTemplate().getName(),
                        todayCompliance,
                        umbral);
            } else {
                log.info("✅ Regla NO disparada — Paciente: {}, Regla: '{}', Cumplimiento: {}% >= Umbral: {}%",
                        patientId,
                        rule.getRuleTemplate().getName(),
                        todayCompliance,
                        umbral);
            }
        }
    }

    // Calcula el cumplimiento del día actual
    private double calculateTodayCompliance(Long patientId) {
        LocalDate today = LocalDate.now();
        return checkInRepository
                .findByPatientIdAndCheckInDate(patientId, today)
                .map(checkIn -> {
                    List<TaskCheckIn> taskCheckIns = taskCheckInRepository
                            .findByCheckInId(checkIn.getId());
                    if (taskCheckIns.isEmpty())
                        return 0.0;
                    long completed = taskCheckIns.stream()
                            .filter(TaskCheckIn::isCompleted).count();
                    return Math.round((completed * 100.0 / taskCheckIns.size()) * 10.0) / 10.0;
                })
                .orElse(0.0);
    }

}
