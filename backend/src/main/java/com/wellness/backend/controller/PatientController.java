package com.wellness.backend.controller;

import com.wellness.backend.dto.request.DeactivatePatientRequest;
import com.wellness.backend.dto.request.PatientListDTO;
import com.wellness.backend.dto.request.ReactivatePatientRequest;
import com.wellness.backend.enums.PatientStatus;
import com.wellness.backend.model.Patient;
import com.wellness.backend.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*") // Permite que React (en otro puerto) se conecte sin bloqueos de CORS
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // POST: Registrar un nuevo paciente
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        Patient savedPatient = patientService.createPatient(patient);
        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    // PATCH: Editar solo datos de contacto
    @PatchMapping("/{id}/contact")
    public ResponseEntity<Patient> updateContact(@PathVariable Long id, @RequestParam String email,
            @RequestParam String phoneNumber) {
        Patient updated = patientService.updateContactInfo(id, email, phoneNumber);
        return ResponseEntity.ok(updated);
    }

    // GET: Listar todos los pacientes activos (por defecto)
    @GetMapping
    public ResponseEntity<List<Patient>> getAll() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // GET: Listar pacientes inactivos
    @GetMapping("/inactive")
    public ResponseEntity<List<Patient>> getInactive() {
        return ResponseEntity.ok(patientService.getInactivePatients());
    }

    // GET: Obtener paciente por id
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    // PATCH: Desactivar paciente - requiere motivo
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Patient> deactivate(@PathVariable Long id,
            @Valid @RequestBody DeactivatePatientRequest request) {
        return ResponseEntity.ok(patientService.deactivatePatient(id, request));
    }

    // PATCH: Reactivar paciente - permite actualizar info básica
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Patient> reactivate(@PathVariable Long id, @RequestBody ReactivatePatientRequest request) {
        return ResponseEntity.ok(patientService.reactivatePatient(id, request));
    }

    @GetMapping("/list")
    public ResponseEntity<List<PatientListDTO>> listPatients(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PatientStatus status,
            @RequestParam(required = false) String condition) {
        return ResponseEntity.ok(patientService.getAllPatientsFiltered(search, status, condition));
    }
}