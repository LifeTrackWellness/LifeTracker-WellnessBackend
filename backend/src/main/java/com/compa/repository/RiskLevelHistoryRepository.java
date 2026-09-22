package com.compa.repository;

import com.compa.enums.RiskLevel;
import com.compa.model.HabitPlan;
import com.compa.model.HealthStatusHistory;
import com.compa.model.Estudiante;
import com.compa.model.RiskLevelHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RiskLevelHistoryRepository extends JpaRepository<RiskLevelHistory, Long> {
    List<RiskLevelHistory> findByEstudianteIdOrderByEvaluatedDateDesc(Long estudianteId);

    Optional<RiskLevelHistory> findTopByEstudianteIdOrderByEvaluatedDateDesc(Long estudianteId);

    boolean existsByEstudianteIdAndEvaluatedDate(Long estudianteId, LocalDate date);

    Optional<RiskLevelHistory> findByEstudianteIdAndEvaluatedDate(Long estudianteId, LocalDate date);

    List<RiskLevelHistory> findTop7ByEstudianteOrderByEvaluatedDateDesc(Estudiante estudiante);
    Long countByEstudianteAndRiskLevel(Estudiante estudiante, RiskLevel riskLevel);

}
