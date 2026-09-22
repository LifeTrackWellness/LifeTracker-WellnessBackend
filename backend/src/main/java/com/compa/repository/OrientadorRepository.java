package com.wellness.backend.repository;

import com.wellness.backend.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long>
{
    Optional<Professional> findByEmail(String email);

    Optional<Professional> findByVerificationToken(String token);

    boolean existsByEmail(String email);

}
