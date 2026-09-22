package com.compa.repository;

import com.compa.model.HealthStatusHistory;
import com.compa.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HealthStatusHistoryRepository extends JpaRepository<HealthStatusHistory, Long> {
    List<HealthStatusHistory> findByClinicalInfoIdOrderByChangedAtDesc(Long clinicalInfoId);

    List<HealthStatusHistory> findByClinicalInfo_EstudianteOrderByChangedAtAsc(Estudiante estudiante);
}
