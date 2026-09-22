package com.wellness.backend.controller;

import com.wellness.backend.dto.request.HabitPlanRequest;
import com.wellness.backend.dto.request.HabitTaskRequest;
import com.wellness.backend.model.HabitPlan;
import com.wellness.backend.model.HabitTask;
import com.wellness.backend.service.HabitPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients/{patientId}/habit-plans")
@CrossOrigin(origins = "*")
public class HabitPlanController {

    private final HabitPlanService habitPlanService;

    public HabitPlanController(HabitPlanService habitPlanService) {
        this.habitPlanService = habitPlanService;
    }

    @PostMapping
    public ResponseEntity<HabitPlan> createPlan(@PathVariable Long patientId,
            @Valid @RequestBody HabitPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitPlanService.createPlan(patientId, request));
    }

    @GetMapping
    public ResponseEntity<List<HabitPlan>> getPlansByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(habitPlanService.getPlansByPatient(patientId));
    }

    @GetMapping("/active")
    public ResponseEntity<HabitPlan> getActivePlan(@PathVariable Long patientId) {
        return ResponseEntity.ok(habitPlanService.getActivePlan(patientId));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<HabitPlan> getPlanById(@PathVariable Long patientId, @PathVariable Long planId) {
        return ResponseEntity.ok(habitPlanService.getPlanById(planId));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<HabitPlan> updatePlan(@PathVariable Long patientId, @PathVariable Long planId,
            @Valid @RequestBody HabitPlanRequest request) {
        return ResponseEntity.ok(habitPlanService.updatePlan(planId, request));
    }

    @PatchMapping("/{planId}/deactivate")
    public ResponseEntity<HabitPlan> deactivatePlan(@PathVariable Long patientId, @PathVariable Long planId) {
        return ResponseEntity.ok(habitPlanService.deactivatePlan(planId));
    }

    @PostMapping("/{planId}/tasks")
    public ResponseEntity<HabitTask> addTask(@PathVariable Long patientId, @PathVariable Long planId,
            @Valid @RequestBody HabitTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitPlanService.addTask(planId, request));
    }

    @DeleteMapping("/{planId}/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long patientId, @PathVariable Long planId,
            @PathVariable Long taskId) {
        habitPlanService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/today")
    public ResponseEntity<List<HabitTask>> getTasksForToday(@PathVariable Long patientId) {
        return ResponseEntity.ok(habitPlanService.getTasksForToday(patientId));
    }
}