package com.wellness.backend.config;


import com.wellness.backend.model.RuleTemplate;
import com.wellness.backend.repository.RuleTemplateRepository;
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
                    "El paciente no ha registrado actividad en X días",
                    3, 1, 5));
            ruleTemplateRepository.save(new RuleTemplate(null,
                    "Sin checklist de tarea",
                    "El paciente no ha completado el checklist en X tareas",
                    3, 1, 5));
            ruleTemplateRepository.save(new RuleTemplate(null,
                    "Baja adherencia al plan",
                    "El paciente ha completado menos del X% de sus tareas",
                    50, 10, 90));
            ruleTemplateRepository.save(new RuleTemplate(null,
                    "Estado emocional bajo",
                    "El paciente ha registrado estado emocional negativo por X días consecutivos",
                    3, 1, 7));
        }
    }
}
