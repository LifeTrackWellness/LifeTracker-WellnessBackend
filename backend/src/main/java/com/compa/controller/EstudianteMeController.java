package com.wellness.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.wellness.backend.dto.request.DailyCheckInRequest;
import com.wellness.backend.dto.response.CheckInDetailResponse;
import com.wellness.backend.dto.response.CheckInSummaryResponse;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.DailyCheckIn;
import com.wellness.backend.model.HabitPlan;
import com.wellness.backend.model.Patient;
import com.wellness.backend.model.PatientConsent;
import com.wellness.backend.repository.PatientRepository;
import com.wellness.backend.service.ConsentService;
import com.wellness.backend.service.DailyCheckInService;
import com.wellness.backend.service.HabitPlanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients/me")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor

public class PatientMeController {
    private final PatientRepository patientRepository;
    private final DailyCheckInService checkInService;
    private final HabitPlanService habitPlanService;
    private final ConsentService consentService;

    // Obtener datos propios del paciente autenticado
    @GetMapping
    public ResponseEntity<Patient> getMe(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(getPatientFromToken(userDetails));
    }

    // Check-in de hoy
    @GetMapping("/check-in/today")
    public ResponseEntity<DailyCheckIn> getTodayCheckIn(
            @AuthenticationPrincipal UserDetails userDetails) {
        Patient patient = getPatientFromToken(userDetails);
        return ResponseEntity.ok(checkInService.getTodayCheckIn(patient.getId()));
    }

    // Crear check-in
    @PostMapping("/check-in")
    public ResponseEntity<DailyCheckIn> createCheckIn(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DailyCheckInRequest request) {
        Patient patient = getPatientFromToken(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checkInService.createCheckIn(patient.getId(), request));
    }

    // Editar check-in
    @PutMapping("/check-in")
    public ResponseEntity<DailyCheckIn> updateCheckIn(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DailyCheckInRequest request) {
        Patient patient = getPatientFromToken(userDetails);
        return ResponseEntity.ok(checkInService.updateCheckIn(patient.getId(), request));
    }

    // Historial últimos 30 días
    @GetMapping("/check-in/last-30-days")
    public ResponseEntity<List<CheckInSummaryResponse>> getLast30Days(
            @AuthenticationPrincipal UserDetails userDetails) {
        Patient patient = getPatientFromToken(userDetails);
        return ResponseEntity.ok(checkInService.getLast30Days(patient.getId()));
    }

    // Detalle de un check-in
    @GetMapping("/check-in/{checkInId}/detail")
    public ResponseEntity<CheckInDetailResponse> getCheckInDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long checkInId) {
        Patient patient = getPatientFromToken(userDetails);
        return ResponseEntity.ok(checkInService.getCheckInDetail(patient.getId(), checkInId));
    }

    // Mensaje de cierre y racha
    @GetMapping("/check-in/closing")
    public ResponseEntity<Map<String, Object>> getClosing(
            @AuthenticationPrincipal UserDetails userDetails) {
        Patient patient = getPatientFromToken(userDetails);
        int streak = checkInService.getCurrentStreak(patient.getId());
        String message = checkInService.getClosingMessage(patient.getId());
        return ResponseEntity.ok(Map.of("streak", streak, "message", message));
    }

    // Tareas del día
    @GetMapping("/check-in/today-tasks")
    public ResponseEntity<?> getTodayTasks(
            @AuthenticationPrincipal UserDetails userDetails) {
        Patient patient = getPatientFromToken(userDetails);
        return ResponseEntity.ok(checkInService.getTodayTasks(patient.getId()));
    }

    // Plan activo
    @GetMapping("/plan")
    public ResponseEntity<HabitPlan> getActivePlan(
            @AuthenticationPrincipal UserDetails userDetails) {
        Patient patient = getPatientFromToken(userDetails);
        return ResponseEntity.ok(habitPlanService.getActivePlan(patient.getId()));
    }

    // Consentimientos
    @GetMapping("/consents")
    public ResponseEntity<List<PatientConsent>> getConsents(
            @AuthenticationPrincipal UserDetails userDetails) {
        Patient patient = getPatientFromToken(userDetails);
        return ResponseEntity.ok(consentService.getConsentsByPatient(patient.getId()));
    }

    // Aceptar consentimiento
    @PatchMapping("/consents/{consentId}/accept")
    public ResponseEntity<PatientConsent> acceptConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long consentId) {
        getPatientFromToken(userDetails); // verifica que es el paciente correcto
        return ResponseEntity.ok(consentService.acceptConsent(consentId));
    }

    // Helper — extrae el paciente del token
    private Patient getPatientFromToken(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
    }

}
