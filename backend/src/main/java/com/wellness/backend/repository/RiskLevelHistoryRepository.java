package com.wellness.backend.repository;

import com.wellness.backend.enums.RiskLevel;
import com.wellness.backend.model.HabitPlan;
import com.wellness.backend.model.HealthStatusHistory;
import com.wellness.backend.model.Patient;
import com.wellness.backend.model.RiskLevelHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RiskLevelHistoryRepository extends JpaRepository<RiskLevelHistory, Long> {
    List<RiskLevelHistory> findByPatientIdOrderByEvaluatedDateDesc(Long patientId);

    Optional<RiskLevelHistory> findTopByPatientIdOrderByEvaluatedDateDesc(Long patientId);

    boolean existsByPatientIdAndEvaluatedDate(Long patientId, LocalDate date);

    Optional<RiskLevelHistory> findByPatientIdAndEvaluatedDate(Long patientId, LocalDate date);

    List<RiskLevelHistory> findTop7ByPatientOrderByEvaluatedDateDesc(Patient patient);
    Long countByPatientAndRiskLevel(Patient patient, RiskLevel riskLevel);

}
