package com.compa.controller;

import com.compa.model.AdherenceSnapshot;
import com.compa.service.AdherenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes/{estudianteId}/adherence")
@CrossOrigin(origins = "*")
public class AdherenceController {

    private final AdherenceService adherenceService;

    public AdherenceController(AdherenceService adherenceService) {
        this.adherenceService = adherenceService;
    }

    @GetMapping("/snapshot")
    public ResponseEntity<AdherenceSnapshot> getLatestSnapshot(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(adherenceService.getLatestSnapshot(estudianteId));
    }

    @GetMapping("/snapshots")
    public ResponseEntity<List<AdherenceSnapshot>> getAllSnapshots(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(adherenceService.getAllSnapshots(estudianteId));
    }
}