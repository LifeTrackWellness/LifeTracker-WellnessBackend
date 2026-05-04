package com.wellness.backend.repository;

import com.wellness.backend.enums.PlanStatus;
import com.wellness.backend.model.HabitPlan;
import com.wellness.backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitPlanRepository extends JpaRepository<HabitPlan, Long> {
    List<HabitPlan> findByPatientId(Long patientId);

    Optional<HabitPlan> findByPatientIdAndStatus(Long patientId, PlanStatus status);

    boolean existsByPatientIdAndStatus(Long patientId, PlanStatus status);

    List<HabitPlan> findByPatient(Patient patient);


}
