package com.wellness.backend.repository;

import com.wellness.backend.model.HabitTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HabitTaskRepository extends JpaRepository<HabitTask, Long> {
    List<HabitTask> findByHabitPlanId(Long habitPlanId);
}