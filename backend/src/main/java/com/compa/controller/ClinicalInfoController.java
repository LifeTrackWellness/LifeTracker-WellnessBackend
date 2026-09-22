package com.compa.controller;

import com.compa.dto.request.ClinicalInfoRequest;
import com.compa.dto.request.HealthStatusUpdateRequest;
import com.compa.enums.HealthStatus;
import com.compa.model.ClinicalInfo;
import com.compa.model.HealthStatusHistory;
import com.compa.service.ClinicalInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/estudiantes/{estudianteId}/clinical-info")
@CrossOrigin(origins = "*")
public class ClinicalInfoController {
    private final ClinicalInfoService clinicalInfoService;

    public ClinicalInfoController(ClinicalInfoService clinicalInfoService) {
        this.clinicalInfoService = clinicalInfoService;
    }

    @PostMapping
    public ResponseEntity<ClinicalInfo> registerClinicalInfo(@PathVariable Long estudianteId,
            @Valid @RequestBody ClinicalInfoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicalInfoService.registerClinicalInfo(estudianteId, request));
    }

    @GetMapping
    public ResponseEntity<ClinicalInfo> getClinicalInfo(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(clinicalInfoService.getClinicalInfo(estudianteId));
    }

    @PutMapping
    public ResponseEntity<ClinicalInfo> updateClinicalInfo(@PathVariable Long estudianteId,
            @Valid @RequestBody ClinicalInfoRequest request) {
        return ResponseEntity.ok(clinicalInfoService.updateClinicalInfo(estudianteId, request));
    }

    @PatchMapping("/health-status")
    public ResponseEntity<ClinicalInfo> updateHealthStatus(@PathVariable Long estudianteId,
            @Valid @RequestBody HealthStatusUpdateRequest request) {
        return ResponseEntity.ok(clinicalInfoService.updateHealthStatus(estudianteId, request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<HealthStatusHistory>> getStatusHistory(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(clinicalInfoService.getStatusHistory(estudianteId));
    }

    @GetMapping("/health-statuses")
    public ResponseEntity<List<HealthStatus>> getHealthStatuses(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(clinicalInfoService.getAvailableHealthStatuses());
    }

}
