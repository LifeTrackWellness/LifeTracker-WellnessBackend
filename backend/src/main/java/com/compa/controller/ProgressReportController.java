package com.compa.controller;

import com.compa.dto.request.ConclusionRequest;
import com.compa.dto.response.ProgressReportResponse;
import com.compa.model.OrientadorNota;
import com.compa.service.ProgressReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estudiantes/{estudianteId}/progress-report")
@RequiredArgsConstructor
public class ProgressReportController
{
    private final ProgressReportService progressReportService;

    @GetMapping
    public ResponseEntity<ProgressReportResponse> getProgressReport(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(progressReportService.getProgressReport(estudianteId));
    }

    @PostMapping("/conclusions")
    public ResponseEntity<OrientadorNota> addConclusion(
            @PathVariable Long estudianteId,
            @Valid @RequestBody ConclusionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progressReportService.addConclusion(estudianteId, request));
    }
}
