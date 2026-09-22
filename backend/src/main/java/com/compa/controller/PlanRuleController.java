package com.wellness.backend.controller;

import com.wellness.backend.model.PlanRule;
import com.wellness.backend.model.RuleEvaluationLog;
import com.wellness.backend.repository.RuleEvaluationLogRepository;
import com.wellness.backend.service.PlanRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*")
public class PlanRuleController {

    private final PlanRuleService planRuleService;
    private final RuleEvaluationLogRepository evaluationLogRepository;

    public PlanRuleController(PlanRuleService planRuleService,
            RuleEvaluationLogRepository evaluationLogRepository) {
        this.planRuleService = planRuleService;
        this.evaluationLogRepository = evaluationLogRepository;
    }

    // Ver reglas configuradas de un plan
    @GetMapping("/{planId}/rules")
    public ResponseEntity<List<PlanRule>> getRulesByPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(planRuleService.getRulesByPlan(planId));
    }

    // Activar o desactivar una regla
    @PatchMapping("/{planId}/rules/{ruleId}/toggle")
    public ResponseEntity<PlanRule> toggleRule(
            @PathVariable Long ruleId,
            @RequestParam boolean active) {
        return ResponseEntity.ok(planRuleService.toggleRule(ruleId, active));
    }

    // Guardar configuración completa del plan
    @PostMapping("/{planId}/rules/save")
    public ResponseEntity<List<PlanRule>> saveConfiguration(
            @PathVariable Long planId,
            @RequestBody List<PlanRule> rules) {
        return ResponseEntity.ok(planRuleService.saveConfiguration(planId, rules));
    }

    // Ver log de evaluaciones de reglas de un plan
    @GetMapping("/{planId}/rules/evaluation-log")
    public ResponseEntity<List<RuleEvaluationLog>> getEvaluationLog(
            @PathVariable Long planId) {
        List<RuleEvaluationLog> logs = evaluationLogRepository.findAll()
                .stream()
                .filter(log -> log.getPlanRule().getHabitPlan().getId().equals(planId))
                .toList();
        return ResponseEntity.ok(logs);
    }
}