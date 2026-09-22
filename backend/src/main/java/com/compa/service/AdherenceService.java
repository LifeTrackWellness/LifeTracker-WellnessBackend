package com.wellness.backend.service;

import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.*;
import com.wellness.backend.repository.*;
import com.wellness.backend.enums.PlanStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class AdherenceService {

    private final AdherenceSnapshotRepository snapshotRepository;
    private final DailyCheckInRepository checkInRepository;
    private final HabitPlanRepository habitPlanRepository;
    private final PatientRepository patientRepository;
    private final TaskCheckInRepository taskCheckInRepository;

    public AdherenceService(
            AdherenceSnapshotRepository snapshotRepository,
            DailyCheckInRepository checkInRepository,
            HabitPlanRepository habitPlanRepository,
            PatientRepository patientRepository,
            TaskCheckInRepository taskCheckInRepository) {
        this.snapshotRepository = snapshotRepository;
        this.checkInRepository = checkInRepository;
        this.habitPlanRepository = habitPlanRepository;
        this.patientRepository = patientRepository;
        this.taskCheckInRepository = taskCheckInRepository;
    }

    @Transactional
    public AdherenceSnapshot calculateAndSave(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));

        LocalDate today = LocalDate.now();

        double weeklyCompliance = calculateCompliance(patientId, today.minusDays(6), today);
        double monthlyCompliance = calculateCompliance(patientId, today.minusDays(29), today);
        int streak = calculateStreak(patientId);
        double consistency = calculateConsistency(patientId, today.minusDays(13), today);

        AdherenceSnapshot snapshot = snapshotRepository
                .findByPatientAndSnapshotDate(patient, today)
                .orElse(new AdherenceSnapshot());

        snapshot.setPatient(patient);
        snapshot.setSnapshotDate(today);
        snapshot.setWeeklyCompliance(weeklyCompliance);
        snapshot.setMonthlyCompliance(monthlyCompliance);
        snapshot.setCurrentStreak(streak);
        snapshot.setConsistency(consistency);

        return snapshotRepository.save(snapshot);
    }

    private double calculateCompliance(Long patientId, LocalDate from, LocalDate to) {
        List<DailyCheckIn> checkIns = checkInRepository
                .findByPatientIdAndCheckInDateBetween(patientId, from, to);

        if (checkIns.isEmpty()) return 0.0;

        long totalTasks = 0;
        long completedTasks = 0;

        for (DailyCheckIn checkIn : checkIns) {
            List<TaskCheckIn> taskCheckIns = taskCheckInRepository
                    .findByCheckInId(checkIn.getId());
            totalTasks += taskCheckIns.size();
            completedTasks += taskCheckIns.stream()
                    .filter(TaskCheckIn::isCompleted)
                    .count();
        }

        if (totalTasks == 0) return 0.0;
        return Math.round((completedTasks * 100.0 / totalTasks) * 10.0) / 10.0;
    }

    private int calculateStreak(Long patientId) {
        List<DailyCheckIn> checkIns = checkInRepository
                .findByPatientIdOrderByCheckInDateDesc(patientId);

        if (checkIns.isEmpty()) return 0;

        int streak = 0;
        LocalDate expected = LocalDate.now();

        for (DailyCheckIn checkIn : checkIns) {
            if (!checkIn.getCheckInDate().equals(expected)) break;

            List<TaskCheckIn> taskCheckIns = taskCheckInRepository
                    .findByCheckInId(checkIn.getId());

            if (taskCheckIns.isEmpty()) break;

            long completed = taskCheckIns.stream()
                    .filter(TaskCheckIn::isCompleted).count();
            double pct = completed * 100.0 / taskCheckIns.size();

            if (pct >= 80.0) {
                streak++;
                expected = expected.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    private double calculateConsistency(Long patientId, LocalDate from, LocalDate to) {
        List<DailyCheckIn> checkIns = checkInRepository
                .findByPatientIdAndCheckInDateBetween(patientId, from, to);

        if (checkIns.size() < 2) return 0.0;

        List<Double> dailyRates = new ArrayList<>();

        for (DailyCheckIn checkIn : checkIns) {
            List<TaskCheckIn> taskCheckIns = taskCheckInRepository
                    .findByCheckInId(checkIn.getId());
            if (!taskCheckIns.isEmpty()) {
                long completed = taskCheckIns.stream()
                        .filter(TaskCheckIn::isCompleted).count();
                dailyRates.add(completed * 100.0 / taskCheckIns.size());
            }
        }

        if (dailyRates.isEmpty()) return 0.0;

        double mean = dailyRates.stream()
                .mapToDouble(Double::doubleValue).average().orElse(0.0);

        double variance = dailyRates.stream()
                .mapToDouble(r -> Math.pow(r - mean, 2))
                .average().orElse(0.0);

        double stdDev = Math.sqrt(variance);

        return Math.max(0.0, Math.round((100.0 - stdDev) * 10.0) / 10.0);
    }

    public AdherenceSnapshot getLatestSnapshot(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));
        return snapshotRepository
                .findByPatientOrderBySnapshotDateDesc(patient)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No hay métricas calculadas aún"));
    }

    public List<AdherenceSnapshot> getAllSnapshots(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));
        return snapshotRepository.findByPatientOrderBySnapshotDateDesc(patient);
    }
}