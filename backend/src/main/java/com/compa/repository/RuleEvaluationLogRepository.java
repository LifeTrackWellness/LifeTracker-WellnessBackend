package com.compa.repository;

import com.compa.model.RuleEvaluationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface RuleEvaluationLogRepository extends JpaRepository<RuleEvaluationLog, Long> {
    boolean existsByPlanRuleIdAndEstudianteIdAndEvaluationDate(
            Long planRuleId, Long estudianteId, LocalDate evaluationDate);

}
