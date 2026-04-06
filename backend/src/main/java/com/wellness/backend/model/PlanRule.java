package com.wellness.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "plan_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="plan_id")
    private HabitPlan habitPlan;


    @Column(name = "umbralp", nullable = false)
    private Integer umbralPersonalizado;

    @Column(nullable = false)
    private boolean active ;

    @ManyToOne
    @JoinColumn(name="rule_id")
    private RuleTemplate ruleTemplate;

}
