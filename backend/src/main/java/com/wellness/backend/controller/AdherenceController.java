package com.wellness.backend.controller;

import com.wellness.backend.model.AdherenceSnapshot;
import com.wellness.backend.service.AdherenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients/{patientId}/adherence")
@CrossOrigin(origins = "*")
public class AdherenceController {

    private final AdherenceService adherenceService;

    public AdherenceController(AdherenceService adherenceService) {
        this.adherenceService = adherenceService;
    }

    @GetMapping("/snapshot")
    public ResponseEntity<AdherenceSnapshot> getLatestSnapshot(@PathVariable Long patientId) {
        return ResponseEntity.ok(adherenceService.getLatestSnapshot(patientId));
    }

    @GetMapping("/snapshots")
    public ResponseEntity<List<AdherenceSnapshot>> getAllSnapshots(@PathVariable Long patientId) {
        return ResponseEntity.ok(adherenceService.getAllSnapshots(patientId));
    }
}