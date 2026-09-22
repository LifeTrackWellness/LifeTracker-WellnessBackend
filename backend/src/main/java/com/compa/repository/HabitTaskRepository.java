package com.compa.repository;

import com.compa.model.HabitTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HabitTaskRepository extends JpaRepository<HabitTask, Long> {
    List<HabitTask> findByHabitPlanId(Long habitPlanId);
}