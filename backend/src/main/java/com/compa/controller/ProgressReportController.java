package com.wellness.backend.controller;

import com.wellness.backend.dto.request.ConclusionRequest;
import com.wellness.backend.dto.response.ProgressReportResponse;
import com.wellness.backend.model.TherapistConclusion;
import com.wellness.backend.service.ProgressReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients/{patientId}/progress-report")
@RequiredArgsConstructor
public class ProgressReportController
{
    private final ProgressReportService progressReportService;

    @GetMapping
    public ResponseEntity<ProgressReportResponse> getProgressReport(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(progressReportService.getProgressReport(patientId));
    }

    @PostMapping("/conclusions")
    public ResponseEntity<TherapistConclusion> addConclusion(
            @PathVariable Long patientId,
            @Valid @RequestBody ConclusionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progressReportService.addConclusion(patientId, request));
    }
}
