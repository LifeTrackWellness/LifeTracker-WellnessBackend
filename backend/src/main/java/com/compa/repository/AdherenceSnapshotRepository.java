package com.compa.repository;

import com.compa.model.AdherenceSnapshot;
import com.compa.model.Estudiante;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AdherenceSnapshotRepository extends JpaRepository<AdherenceSnapshot, Long>

{
    Optional<AdherenceSnapshot> findByEstudianteAndSnapshotDate(Estudiante estudiante, LocalDate date);

    List<AdherenceSnapshot> findByEstudianteOrderBySnapshotDateDesc(Estudiante estudiante);

}
