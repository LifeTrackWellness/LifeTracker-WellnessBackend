package com.wellness.backend.controller;

import com.wellness.backend.dto.response.AlertResponse;
import com.wellness.backend.service.AlertService;
import com.wellness.backend.service.JwtService;
import com.wellness.backend.repository.ProfessionalRepository;
import com.wellness.backend.model.Alert;
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
    private final ProfessionalRepository professionalRepository;

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAlerts(
            @RequestHeader("Authorization") String authHeader) {
        Long professionalId = getProfessionalId(authHeader);
        List<AlertResponse> alerts = alertService
                .getAlertsByProfessional(professionalId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader("Authorization") String authHeader) {
        Long professionalId = getProfessionalId(authHeader);
        return ResponseEntity.ok(Map.of("count",
                alertService.getUnreadCount(professionalId)));
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
                .patientId(alert.getPatient().getId())
                .patientName(alert.getPatient().getName()
                        + " " + alert.getPatient().getLastName())
                .type(alert.getType())
                .status(alert.getStatus())
                .description(alert.getDescription())
                .createdAt(alert.getCreatedAt())
                .resolvedAt(alert.getResolvedAt())
                .build();
    }

    private Long getProfessionalId(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        return professionalRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"))
                .getId();
    }
}