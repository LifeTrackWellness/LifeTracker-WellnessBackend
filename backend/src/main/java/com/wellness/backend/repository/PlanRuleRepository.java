package com.wellness.backend.repository;

import com.wellness.backend.model.HabitPlan;
import com.wellness.backend.model.PlanRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRuleRepository extends JpaRepository<PlanRule,Long> {

    List<PlanRule> findAllByHabitPlan(HabitPlan habitPlan);

}



