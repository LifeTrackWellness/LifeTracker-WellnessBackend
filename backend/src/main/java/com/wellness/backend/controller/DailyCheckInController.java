package com.wellness.backend.controller;

import com.wellness.backend.dto.request.DailyCheckInRequest;
import com.wellness.backend.enums.EmotionalState;
import com.wellness.backend.model.DailyCheckIn;
import com.wellness.backend.model.HabitTask;
import com.wellness.backend.service.DailyCheckInService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients/{patientId}/check-in")
@CrossOrigin(origins = "*")
public class DailyCheckInController {

    private final DailyCheckInService checkInService;

    public DailyCheckInController(DailyCheckInService checkInService) {
        this.checkInService = checkInService;
    }

    // Paso 1: Obtener estados emocionales disponibles
    @GetMapping("/emotional-states")
    public ResponseEntity<List<EmotionalState>> getEmotionalStates(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(checkInService.getEmotionalStates());
    }

    // Paso 2: Obtener tareas del día
    @GetMapping("/today-tasks")
    public ResponseEntity<List<HabitTask>> getTodayTasks(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(checkInService.getTodayTasks(patientId));
    }

    // Crear check-in del día
    @PostMapping
    public ResponseEntity<DailyCheckIn> createCheckIn(
            @PathVariable Long patientId,
            @Valid @RequestBody DailyCheckInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checkInService.createCheckIn(patientId, request));
    }

    // Editar check-in del día (hasta 23:59)
    @PutMapping
    public ResponseEntity<DailyCheckIn> updateCheckIn(
            @PathVariable Long patientId,
            @Valid @RequestBody DailyCheckInRequest request) {
        return ResponseEntity.ok(checkInService.updateCheckIn(patientId, request));
    }

    // Obtener check-in de hoy
    @GetMapping("/today")
    public ResponseEntity<DailyCheckIn> getTodayCheckIn(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(checkInService.getTodayCheckIn(patientId));
    }

    // Paso 3: Obtener mensaje de cierre y racha
    @GetMapping("/closing")
    public ResponseEntity<Map<String, Object>> getClosingInfo(
            @PathVariable Long patientId) {
        int streak = checkInService.getCurrentStreak(patientId);
        String message = checkInService.getClosingMessage(patientId);
        return ResponseEntity.ok(Map.of(
                "streak", streak,
                "message", message));
    }

    // Historial de check-ins
    @GetMapping("/history")
    public ResponseEntity<List<DailyCheckIn>> getHistory(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(checkInService.getHistory(patientId));
    }
}