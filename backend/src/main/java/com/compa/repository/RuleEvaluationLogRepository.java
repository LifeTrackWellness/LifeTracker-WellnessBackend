package com.wellness.backend.repository;

import com.wellness.backend.model.RuleEvaluationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface RuleEvaluationLogRepository extends JpaRepository<RuleEvaluationLog, Long> {
    boolean existsByPlanRuleIdAndPatientIdAndEvaluationDate(
            Long planRuleId, Long patientId, LocalDate evaluationDate);

}
