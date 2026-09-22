package com.wellness.backend.controller;


import com.wellness.backend.model.RuleTemplate;
import com.wellness.backend.repository.RuleTemplateRepository;
import com.wellness.backend.service.RuleTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rule-templates")
@CrossOrigin(origins = "*")

public class RuleTemplateController {

    private final  RuleTemplateService ruleTemplateService;

    public RuleTemplateController(RuleTemplateService ruleTemplateService) {this.ruleTemplateService = ruleTemplateService;}



    @GetMapping
    public ResponseEntity<List<RuleTemplate>> getAllRuleTemplates() {
        return ResponseEntity.ok(ruleTemplateService.getAllPlanTemplates());
    }

}
