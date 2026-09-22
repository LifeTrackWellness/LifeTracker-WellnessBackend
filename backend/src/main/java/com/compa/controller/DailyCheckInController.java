package com.compa.controller;

import com.compa.dto.request.DailyCheckInRequest;
import com.compa.dto.response.CheckInDetailResponse;
import com.compa.dto.response.CheckInSummaryResponse;
import com.compa.enums.EmotionalState;
import com.compa.model.DailyCheckIn;
import com.compa.model.HabitTask;
import com.compa.service.DailyCheckInService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estudiantes/{estudianteId}/check-in")
@CrossOrigin(origins = "*")
public class DailyCheckInController {

    private final DailyCheckInService checkInService;

    public DailyCheckInController(DailyCheckInService checkInService) {
        this.checkInService = checkInService;
    }

    // Paso 1: Obtener estados emocionales disponibles
    @GetMapping("/emotional-states")
    public ResponseEntity<List<EmotionalState>> getEmotionalStates(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(checkInService.getEmotionalStates());
    }

    // Paso 2: Obtener tareas del día
    @GetMapping("/today-tasks")
    public ResponseEntity<List<HabitTask>> getTodayTasks(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(checkInService.getTodayTasks(estudianteId));
    }

    // Crear check-in del día
    @PostMapping
    public ResponseEntity<DailyCheckIn> createCheckIn(
            @PathVariable Long estudianteId,
            @Valid @RequestBody DailyCheckInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checkInService.createCheckIn(estudianteId, request));
    }

    // Editar check-in del día (hasta 23:59)
    @PutMapping
    public ResponseEntity<DailyCheckIn> updateCheckIn(
            @PathVariable Long estudianteId,
            @Valid @RequestBody DailyCheckInRequest request) {
        return ResponseEntity.ok(checkInService.updateCheckIn(estudianteId, request));
    }

    // Obtener check-in de hoy
    @GetMapping("/today")
    public ResponseEntity<DailyCheckIn> getTodayCheckIn(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(checkInService.getTodayCheckIn(estudianteId));
    }

    // Paso 3: Obtener mensaje de cierre y racha
    @GetMapping("/closing")
    public ResponseEntity<Map<String, Object>> getClosingInfo(
            @PathVariable Long estudianteId) {
        int streak = checkInService.getCurrentStreak(estudianteId);
        String message = checkInService.getClosingMessage(estudianteId);
        return ResponseEntity.ok(Map.of(
                "streak", streak,
                "message", message));
    }

    // Historial de check-ins
    @GetMapping("/history")
    public ResponseEntity<List<DailyCheckIn>> getHistory(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(checkInService.getHistory(estudianteId));
    }

    // Últimos 30 días
    @GetMapping("/last-30-days")
    public ResponseEntity<List<CheckInSummaryResponse>> getLast30Days(
            @PathVariable Long estudianteId) {
        return ResponseEntity.ok(checkInService.getLast30Days(estudianteId));
    }

    // Detalle de un día específico
    @GetMapping("/{checkInId}/detail")
    public ResponseEntity<CheckInDetailResponse> getCheckInDetail(
            @PathVariable Long estudianteId,
            @PathVariable Long checkInId) {
        return ResponseEntity.ok(checkInService.getCheckInDetail(estudianteId, checkInId));
    }
}