package com.compa.repository;

import com.compa.model.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    Optional<Guardian> findByEstudianteId(Long estudianteId);

    boolean existsByEstudianteId(Long estudianteId);
}
