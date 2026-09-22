package com.wellness.backend.repository;

import com.wellness.backend.model.HealthStatusHistory;
import com.wellness.backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HealthStatusHistoryRepository extends JpaRepository<HealthStatusHistory, Long> {
    List<HealthStatusHistory> findByClinicalInfoIdOrderByChangedAtDesc(Long clinicalInfoId);

    List<HealthStatusHistory> findByClinicalInfo_PatientOrderByChangedAtAsc(Patient patient);
}
