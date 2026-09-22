package com.compa.controller;

import com.compa.model.EstudianteConsent;
import com.compa.service.ConsentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes/{estudianteId}/consents")
@CrossOrigin(origins = "*")
public class ConsentController
{
    private final ConsentService consentService;
    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @GetMapping
    public ResponseEntity<List>getConsentsByEstudianteId(@PathVariable Long estudianteId)
    {        List<EstudianteConsent> consents = consentService.getConsentsByEstudiante(estudianteId);
        return ResponseEntity.ok(consents);
    }

    @GetMapping("/pending")
    public ResponseEntity<Boolean>getPendingConsentsByEstudianteId(@PathVariable Long estudianteId)
    {
        return ResponseEntity.ok(consentService.hasPendingConsents(estudianteId));
    }

    @PatchMapping("/{consentId}/accept")
    public ResponseEntity<EstudianteConsent> acceptConsent(@PathVariable Long consentId)
    {
        return ResponseEntity.ok(consentService.acceptConsent(consentId));

    }


}
