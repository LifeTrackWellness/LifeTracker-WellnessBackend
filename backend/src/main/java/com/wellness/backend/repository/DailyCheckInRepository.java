package com.wellness.backend.repository;

import com.wellness.backend.model.DailyCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyCheckInRepository extends JpaRepository<DailyCheckIn, Long> {
    Optional<DailyCheckIn> findByPatientIdAndCheckInDate(Long patientId, LocalDate date);

    boolean existsByPatientIdAndCheckInDate(Long patientId, LocalDate date);

    List<DailyCheckIn> findByPatientIdOrderByCheckInDateDesc(Long patientId);

}
