package com.compa.controller;

import com.compa.dto.request.GuardianRequest;
import com.compa.dto.response.GuardianResponse;
import com.compa.service.GuardianService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudiantes/{estudianteId}/guardian")
@CrossOrigin(origins = "*")
public class GuardianController {
    private final GuardianService guardianService;

    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    @PostMapping
    public ResponseEntity<GuardianResponse> createGuardian(
            @PathVariable Long estudianteId,
            @Valid @RequestBody GuardianRequest request) {
        return new ResponseEntity<>(guardianService.saveGuardian(estudianteId, request), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<GuardianResponse> updateGuardian(
            @PathVariable Long estudianteId,
            @Valid @RequestBody GuardianRequest request) {
        return ResponseEntity.ok(guardianService.saveGuardian(estudianteId, request));
    }

    @GetMapping
    public ResponseEntity<GuardianResponse> getGuardian(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(guardianService.getGuardianByEstudiante(estudianteId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteGuardian(@PathVariable Long estudianteId) {
        guardianService.deleteGuardian(estudianteId);
        return ResponseEntity.noContent().build();
    }

}
