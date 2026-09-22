package com.wellness.backend.repository;

import com.wellness.backend.model.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    Optional<Guardian> findByPatientId(Long patientId);

    boolean existsByPatientId(Long patientId);
}
