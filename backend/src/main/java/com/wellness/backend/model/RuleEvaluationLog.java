package com.wellness.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rule_evaluation_log")
@Data
@NoArgsConstructor
public class RuleEvaluationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_rule_id", nullable = false)
    private PlanRule planRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "evaluation_date", nullable = false)
    private LocalDate evaluationDate;

    @Column(name = "triggered", nullable = false)
    private boolean triggered;

    @Column(name = "compliance_value", nullable = false)
    private double complianceValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.evaluationDate == null)
            this.evaluationDate = LocalDate.now();
    }

}
