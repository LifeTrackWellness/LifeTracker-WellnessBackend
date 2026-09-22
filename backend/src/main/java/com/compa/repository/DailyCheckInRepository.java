package com.compa.repository;

import com.compa.model.DailyCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyCheckInRepository extends JpaRepository<DailyCheckIn, Long> {
    Optional<DailyCheckIn> findByEstudianteIdAndCheckInDate(Long estudianteId, LocalDate date);

    boolean existsByEstudianteIdAndCheckInDate(Long estudianteId, LocalDate date);

    List<DailyCheckIn> findByEstudianteIdOrderByCheckInDateDesc(Long estudianteId);

    List<DailyCheckIn> findByEstudianteIdAndCheckInDateBetween(Long estudianteId, LocalDate from, LocalDate to);

}
