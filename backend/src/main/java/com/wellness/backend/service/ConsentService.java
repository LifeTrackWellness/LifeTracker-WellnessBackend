package com.wellness.backend.service;

import com.wellness.backend.model.ConsentTemplate;
import com.wellness.backend.model.Patient;
import com.wellness.backend.model.PatientConsent;
import com.wellness.backend.repository.ConsentTemplateRepository;
import com.wellness.backend.repository.PatientConsentRepository;
import com.wellness.backend.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsentService
{
    private final PatientConsentRepository patientConsentRepository;
    private final ConsentTemplateRepository consentTemplateRepository;
    private final PatientRepository patientRepository;

    public ConsentService(PatientConsentRepository patientConsentRepository,
                          ConsentTemplateRepository consentTemplateRepository,
                          PatientRepository patientRepository) {
        this.patientConsentRepository = patientConsentRepository;
        this.consentTemplateRepository = consentTemplateRepository;
        this.patientRepository = patientRepository;
    }


    @Transactional
    public void generateConsentsForPatient(Patient patient) {
        List<ConsentTemplate> templates = consentTemplateRepository.findAll();
        List<PatientConsent> consents = new ArrayList<>();
        for (ConsentTemplate template : templates) {
            PatientConsent consent = new PatientConsent();
            consent.setPatient(patient);
            consent.setConsentTemplate(template);
            consent.setAceptado(false);
            consent.setFechaAceptacion(null);
            consents.add(consent);
        }
        patientConsentRepository.saveAll(consents);
    }

    @Transactional(readOnly = true)
    public List<PatientConsent> getConsentsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + patientId));
        return patientConsentRepository.findByPatient(patient);
    }

    @Transactional(readOnly = true)
    public boolean hasPendingConsents(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + patientId));
        List<PatientConsent> pending = patientConsentRepository
                .findByPatientAndAceptado(patient, false);
        return !pending.isEmpty();
    }

    @Transactional
    public PatientConsent acceptConsent(Long consentId) {
        PatientConsent consent = patientConsentRepository.findById(consentId)
                .orElseThrow(() -> new RuntimeException("Consentimiento no encontrado: " + consentId));
        consent.setAceptado(true);
        consent.setFechaAceptacion(LocalDateTime.now());
        return patientConsentRepository.save(consent);
    }


}
