package com.wellness.backend.repository;

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
}
