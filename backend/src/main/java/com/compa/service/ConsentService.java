package com.compa.service;

import com.compa.model.ConsentTemplate;
import com.compa.model.Estudiante;
import com.compa.model.EstudianteConsent;
import com.compa.repository.ConsentTemplateRepository;
import com.compa.repository.EstudianteConsentRepository;
import com.compa.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsentService {
    private final EstudianteConsentRepository estudianteConsentRepository;
    private final ConsentTemplateRepository consentTemplateRepository;
    private final EstudianteRepository estudianteRepository;

    public ConsentService(EstudianteConsentRepository estudianteConsentRepository,
            ConsentTemplateRepository consentTemplateRepository,
            EstudianteRepository estudianteRepository) {
        this.estudianteConsentRepository = estudianteConsentRepository;
        this.consentTemplateRepository = consentTemplateRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Transactional
    public void generateConsentsForEstudiante(Estudiante estudiante) {
        List<ConsentTemplate> templates = consentTemplateRepository.findAll();
        List<EstudianteConsent> consents = new ArrayList<>();
        for (ConsentTemplate template : templates) {
            EstudianteConsent consent = new EstudianteConsent();
            consent.setEstudiante(estudiante);
            consent.setConsentTemplate(template);
            consent.setAceptado(false);
            consent.setFechaAceptacion(null);
            consents.add(consent);
        }
        estudianteConsentRepository.saveAll(consents);
    }

    @Transactional(readOnly = true)
    public List<EstudianteConsent> getConsentsByEstudiante(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + estudianteId));
        return estudianteConsentRepository.findByEstudiante(estudiante);
    }

    @Transactional(readOnly = true)
    public boolean hasPendingConsents(Long estudianteId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + estudianteId));
        List<EstudianteConsent> pending = estudianteConsentRepository
                .findByEstudianteAndAceptado(estudiante, false);
        return !pending.isEmpty();
    }

    @Transactional
    public EstudianteConsent acceptConsent(Long consentId) {
        EstudianteConsent consent = estudianteConsentRepository.findById(consentId)
                .orElseThrow(() -> new RuntimeException("Consentimiento no encontrado: " + consentId));
        consent.setAceptado(true);
        consent.setFechaAceptacion(LocalDateTime.now());
        return estudianteConsentRepository.save(consent);
    }

}
