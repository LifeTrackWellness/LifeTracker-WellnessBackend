package com.compa.repository;

import com.compa.model.TaskCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskCheckInRepository extends JpaRepository<TaskCheckIn, Long> {
    List<TaskCheckIn> findByCheckInId(Long checkInId);

    Optional<TaskCheckIn> findByCheckInIdAndTaskId(Long checkInId, Long taskId);
}