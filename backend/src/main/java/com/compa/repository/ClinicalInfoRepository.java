package com.compa.repository;

import com.compa.model.ClinicalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClinicalInfoRepository extends JpaRepository<ClinicalInfo, Long> {
    Optional<ClinicalInfo> findByEstudianteId(Long estudianteId);

    boolean existsByEstudianteId(Long estudianteId);
}