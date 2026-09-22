package com.wellness.backend.controller;

import com.wellness.backend.dto.request.ClinicalInfoRequest;
import com.wellness.backend.dto.request.HealthStatusUpdateRequest;
import com.wellness.backend.enums.HealthStatus;
import com.wellness.backend.model.ClinicalInfo;
import com.wellness.backend.model.HealthStatusHistory;
import com.wellness.backend.service.ClinicalInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients/{patientId}/clinical-info")
@CrossOrigin(origins = "*")
public class ClinicalInfoController {
    private final ClinicalInfoService clinicalInfoService;

    public ClinicalInfoController(ClinicalInfoService clinicalInfoService) {
        this.clinicalInfoService = clinicalInfoService;
    }

    @PostMapping
    public ResponseEntity<ClinicalInfo> registerClinicalInfo(@PathVariable Long patientId,
            @Valid @RequestBody ClinicalInfoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicalInfoService.registerClinicalInfo(patientId, request));
    }

    @GetMapping
    public ResponseEntity<ClinicalInfo> getClinicalInfo(@PathVariable Long patientId) {
        return ResponseEntity.ok(clinicalInfoService.getClinicalInfo(patientId));
    }

    @PutMapping
    public ResponseEntity<ClinicalInfo> updateClinicalInfo(@PathVariable Long patientId,
            @Valid @RequestBody ClinicalInfoRequest request) {
        return ResponseEntity.ok(clinicalInfoService.updateClinicalInfo(patientId, request));
    }

    @PatchMapping("/health-status")
    public ResponseEntity<ClinicalInfo> updateHealthStatus(@PathVariable Long patientId,
            @Valid @RequestBody HealthStatusUpdateRequest request) {
        return ResponseEntity.ok(clinicalInfoService.updateHealthStatus(patientId, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<HealthStatusHistory>> getStatusHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(clinicalInfoService.getStatusHistory(patientId));
    }

    @GetMapping("/health-statuses")
    public ResponseEntity<List<HealthStatus>> getHealthStatuses(@PathVariable Long patientId) {
        return ResponseEntity.ok(clinicalInfoService.getAvailableHealthStatuses());
    }

}
