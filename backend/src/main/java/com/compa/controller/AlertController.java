package com.compa.controller;

import com.compa.dto.response.AlertResponse;
import com.compa.service.AlertService;
import com.compa.service.JwtService;
import com.compa.repository.OrientadorRepository;
import com.compa.model.Alert;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final JwtService jwtService;
    private final OrientadorRepository orientadorRepository;

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(
            @RequestHeader("Authorization") String authHeader) {
        Long orientadorId = getOrientadorId(authHeader);
        List<AlertResponse> alerts = alertService
                .getAlertsByOrientador(orientadorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader("Authorization") String authHeader) {
        Long orientadorId = getOrientadorId(authHeader);
        return ResponseEntity.ok(Map.of("count",
                alertService.getUnreadCount(orientadorId)));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<AlertResponse> resolveAlert(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(alertService.resolveAlert(id)));
    }

    @PatchMapping("/{id}/ignore")
    public ResponseEntity<AlertResponse> ignoreAlert(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(alertService.ignoreAlert(id)));
    }

    private AlertResponse toResponse(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .estudianteId(alert.getEstudiante().getId())
                .estudianteName(alert.getEstudiante().getName()
                        + " " + alert.getEstudiante().getLastName())
                .type(alert.getType())
                .status(alert.getStatus())
                .description(alert.getDescription())
                .createdAt(alert.getCreatedAt())
                .resolvedAt(alert.getResolvedAt())
                .build();
    }

    private Long getOrientadorId(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        return orientadorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Orientador no encontrado"))
                .getId();
    }
}