package com.wellness.backend.controller;

import com.wellness.backend.dto.request.GuardianRequest;
import com.wellness.backend.dto.response.GuardianResponse;
import com.wellness.backend.service.GuardianService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients/{patientId}/guardian")
@CrossOrigin(origins = "*")
public class GuardianController {
    private final GuardianService guardianService;

    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    @PostMapping
    public ResponseEntity<GuardianResponse> createGuardian(
            @PathVariable Long patientId,
            @Valid @RequestBody GuardianRequest request) {
        return new ResponseEntity<>(guardianService.saveGuardian(patientId, request), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<GuardianResponse> updateGuardian(
            @PathVariable Long patientId,
            @Valid @RequestBody GuardianRequest request) {
        return ResponseEntity.ok(guardianService.saveGuardian(patientId, request));
    }

    @GetMapping
    public ResponseEntity<GuardianResponse> getGuardian(@PathVariable Long patientId) {
        return ResponseEntity.ok(guardianService.getGuardianByPatient(patientId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteGuardian(@PathVariable Long patientId) {
        guardianService.deleteGuardian(patientId);
        return ResponseEntity.noContent().build();
    }

}
