package com.compa.repository;

import com.compa.enums.PlanStatus;
import com.compa.model.HabitPlan;
import com.compa.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitPlanRepository extends JpaRepository<HabitPlan, Long> {
    List<HabitPlan> findByEstudianteId(Long estudianteId);

    Optional<HabitPlan> findByEstudianteIdAndStatus(Long estudianteId, PlanStatus status);

    boolean existsByEstudianteIdAndStatus(Long estudianteId, PlanStatus status);

    List<HabitPlan> findByEstudiante(Estudiante estudiante);


}
