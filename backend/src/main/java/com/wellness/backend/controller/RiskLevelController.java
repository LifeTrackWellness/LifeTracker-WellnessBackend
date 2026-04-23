package com.wellness.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wellness.backend.dto.response.RiskLevelHistoryResponse;
import com.wellness.backend.dto.response.RiskLevelResponse;
import com.wellness.backend.service.RiskLevelService;

@RestController
@CrossOrigin(origins = "*")
public class RiskLevelController {
    private final RiskLevelService riskLevelService;

    public RiskLevelController(RiskLevelService riskLevelService) {
        this.riskLevelService = riskLevelService;
    }

    // Obtener nivel de riesgo actual de un paciente
    @GetMapping("/api/patients/{patientId}/risk-level")
    public ResponseEntity<RiskLevelResponse> getCurrentRiskLevel(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(riskLevelService.getCurrentRiskLevel(patientId));
    }

    // Evaluar y guardar nivel de riesgo de un paciente
    @PostMapping("/api/patients/{patientId}/risk-level/evaluate")
    public ResponseEntity<RiskLevelResponse> evaluatePatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(riskLevelService.evaluatePatient(patientId));
    }

    // Historial de niveles de riesgo de un paciente
    @GetMapping("/api/patients/{patientId}/risk-level/history")
    public ResponseEntity<List<RiskLevelHistoryResponse>> getRiskLevelHistory(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(riskLevelService.getRiskLevelHistory(patientId));
    }

    // Evaluar todos los pacientes activos (disparo manual o scheduler)
    @PostMapping("/api/risk-level/evaluate-all")
    public ResponseEntity<List<RiskLevelResponse>> evaluateAllPatients() {
        return ResponseEntity.ok(riskLevelService.evaluateAllActivePatients());
    }

    // Ver nivel de riesgo de todos los pacientes activos
    @GetMapping("/api/risk-level/all")
    public ResponseEntity<List<RiskLevelResponse>> getAllPatientsRiskLevel() {
        return ResponseEntity.ok(riskLevelService.getAllPatientsRiskLevel());
    }

}
