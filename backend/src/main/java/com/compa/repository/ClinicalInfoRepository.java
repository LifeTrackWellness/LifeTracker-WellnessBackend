package com.wellness.backend.repository;

import com.wellness.backend.model.ClinicalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClinicalInfoRepository extends JpaRepository<ClinicalInfo, Long> {
    Optional<ClinicalInfo> findByPatientId(Long patientId);

    boolean existsByPatientId(Long patientId);
}