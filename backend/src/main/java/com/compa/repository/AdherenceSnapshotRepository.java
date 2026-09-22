package com.wellness.backend.repository;

import com.wellness.backend.model.AdherenceSnapshot;
import com.wellness.backend.model.Patient;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AdherenceSnapshotRepository extends JpaRepository<AdherenceSnapshot, Long>

{
    Optional<AdherenceSnapshot> findByPatientAndSnapshotDate(Patient patient, LocalDate date);

    List<AdherenceSnapshot> findByPatientOrderBySnapshotDateDesc(Patient patient);

}
