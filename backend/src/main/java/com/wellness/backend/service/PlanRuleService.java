package com.wellness.backend.service;

import com.wellness.backend.model.HabitPlan;
import com.wellness.backend.model.PlanRule;
import com.wellness.backend.repository.HabitPlanRepository;
import com.wellness.backend.repository.HabitTaskRepository;
import com.wellness.backend.repository.PlanRuleRepository;
import com.wellness.backend.repository.RuleTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlanRuleService {
    private final PlanRuleRepository planRuleRepository;
    private final HabitPlanRepository habitPlanRepository;
    private final RuleTemplateRepository  ruleTemplateRepository;


    public PlanRuleService(PlanRuleRepository planRuleRepository,
                           HabitPlanRepository habitPlanRepository,
                           RuleTemplateRepository ruleTemplateRepository)
            {this.planRuleRepository = planRuleRepository;
            this.habitPlanRepository = habitPlanRepository;
            this.ruleTemplateRepository = ruleTemplateRepository;}



// Criterio: Muestra las reglas configuradas de un plan
@Transactional(readOnly = true)
public List<PlanRule> getRulesByPlan(Long planId)
{
    HabitPlan plan = habitPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + planId));
    return planRuleRepository.findAllByHabitPlan(plan);
}


// Criterio: Activar o desactivar mediante un switch
@Transactional
public PlanRule toggleRule(Long planRuleId, boolean active)

// Buscar la regla, si no existe lanzar excepción
{
    PlanRule rule = planRuleRepository.findById(planRuleId)
            .orElseThrow(() -> new RuntimeException("Regla no encontrada con id: " + planRuleId));

    // Cambiar el estado
    rule.setActive(active);

    // Guardar en Neon y retornar
    return planRuleRepository.save(rule);
}


// Guardar configuración completa del plan
@Transactional
public List<PlanRule> saveConfiguration(Long planId, List<PlanRule> rules) {
    HabitPlan plan = habitPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + planId));

    for (PlanRule rule : rules) {
        rule.setHabitPlan(plan);
        if (rule.getId() != null && rule.getId() == 0) {
            rule.setId(null);
        }
    }
    return planRuleRepository.saveAll(rules);
}

}