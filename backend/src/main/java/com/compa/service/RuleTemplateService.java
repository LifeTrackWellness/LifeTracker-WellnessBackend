package com.compa.service;

import com.compa.model.RuleTemplate;
import com.compa.repository.RuleTemplateRepository;
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

