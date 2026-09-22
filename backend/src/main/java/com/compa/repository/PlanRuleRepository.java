package com.compa.repository;

import com.compa.model.HabitPlan;
import com.compa.model.PlanRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRuleRepository extends JpaRepository<PlanRule,Long> {

    List<PlanRule> findAllByHabitPlan(HabitPlan habitPlan);

}



