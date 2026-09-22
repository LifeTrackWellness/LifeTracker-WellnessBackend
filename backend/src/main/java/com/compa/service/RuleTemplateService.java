package com.wellness.backend.service;

import com.wellness.backend.model.RuleTemplate;
import com.wellness.backend.repository.RuleTemplateRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleTemplateService {
    private final RuleTemplateRepository ruleTemplateRepository;

    public RuleTemplateService(RuleTemplateRepository ruleTemplateRepository) {
        this.ruleTemplateRepository = ruleTemplateRepository;
    }

    @Transactional(readOnly = true)

    public List<RuleTemplate> getAllPlanTemplates() {
        return ruleTemplateRepository.findAll();
    }
}

