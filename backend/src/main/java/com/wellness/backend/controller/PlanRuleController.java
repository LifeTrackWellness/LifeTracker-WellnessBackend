package com.wellness.backend.controller;

import com.wellness.backend.model.PlanRule;
import com.wellness.backend.service.PlanRuleService;
import com.wellness.backend.service.RuleTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*")
public class PlanRuleController {

    private final PlanRuleService planRuleService;

    public PlanRuleController(PlanRuleService planRuleService) {
        this.planRuleService = planRuleService;
    }

    @GetMapping("/{planId}/rules")
    public ResponseEntity<List<PlanRule>> getRulesByPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(planRuleService.getRulesByPlan(planId));
    }

    @PatchMapping("/{planId}/rules/{ruleId}/toggle")
    public ResponseEntity<PlanRule> toggleRule(
            @PathVariable Long ruleId,
            @RequestParam boolean active) {
        return ResponseEntity.ok(planRuleService.toggleRule(ruleId, active));
    }

    @PostMapping("/{planId}/rules/save")
    public ResponseEntity<List<PlanRule>> saveConfiguration(
            @PathVariable Long planId,
            @RequestBody List<PlanRule> rules) {
        return ResponseEntity.ok(planRuleService.saveConfiguration(planId, rules));
    }
}
