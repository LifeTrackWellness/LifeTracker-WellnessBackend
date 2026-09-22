package com.compa.config;


import com.compa.model.RuleTemplate;
import com.compa.repository.RuleTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RuleTemplateRepository ruleTemplateRepository;

    public DataInitializer(RuleTemplateRepository ruleTemplateRepository) {
        this.ruleTemplateRepository = ruleTemplateRepository;
    }

    @Override
    public void run(String... args) {
        long count = ruleTemplateRepository.count();
        System.out.println(">>> RuleTemplate count: " + count);

        if (count == 0) {
            System.out.println(">>> Insertando plantillas...");
            ruleTemplateRepository.save(new RuleTemplate(null,
                    "Alerta por inactividad",
                    "El estudiante no ha registrado actividad en X días",
                    3, 1, 5));
            ruleTemplateRepository.save(new RuleTemplate(null,
                    "Sin checklist de tarea",
                    "El estudiante no ha completado el checklist en X tareas",
                    3, 1, 5));
            ruleTemplateRepository.save(new RuleTemplate(null,
                    "Baja adherencia al plan",
                    "El estudiante ha completado menos del X% de sus tareas",
                    50, 10, 90));
            ruleTemplateRepository.save(new RuleTemplate(null,
                    "Estado emocional bajo",
                    "El estudiante ha registrado estado emocional negativo por X días consecutivos",
                    3, 1, 7));
        }
    }
}
