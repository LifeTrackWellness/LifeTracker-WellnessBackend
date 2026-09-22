package com.wellness.backend.model;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wellness.backend.model.TherapistConclusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TherapistConclusionRepository extends JpaRepository<TherapistConclusion, Long> {
    List<TherapistConclusion> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}