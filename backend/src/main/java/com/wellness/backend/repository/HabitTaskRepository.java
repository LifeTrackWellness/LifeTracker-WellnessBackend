package com.wellness.backend.repository;
import com.wellness.backend.model.HabitTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitTaskRepository extends JpaRepository<HabitTask, Long> {
}