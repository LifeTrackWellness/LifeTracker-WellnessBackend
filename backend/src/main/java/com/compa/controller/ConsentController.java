package com.wellness.backend.controller;

import com.wellness.backend.model.PatientConsent;
import com.wellness.backend.service.ConsentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients/{patientId}/consents")
@CrossOrigin(origins = "*")
public class ConsentController
{
    private final ConsentService consentService;
    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @GetMapping
    public ResponseEntity<List>getConsentsByPatientId(@PathVariable Long patientId)
    {        List<PatientConsent> consents = consentService.getConsentsByPatient(patientId);
        return ResponseEntity.ok(consents);
    }

    @GetMapping("/pending")
    public ResponseEntity<Boolean>getPendingConsentsByPatientId(@PathVariable Long patientId)
    {
        return ResponseEntity.ok(consentService.hasPendingConsents(patientId));
    }

    @PatchMapping("/{consentId}/accept")
    public ResponseEntity<PatientConsent> acceptConsent(@PathVariable Long consentId)
    {
        return ResponseEntity.ok(consentService.acceptConsent(consentId));

    }


}
