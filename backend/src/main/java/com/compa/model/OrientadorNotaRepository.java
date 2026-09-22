package com.compa.model;

import org.springframework.data.jpa.repository.JpaRepository;
import com.compa.model.OrientadorNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrientadorNotaRepository extends JpaRepository<OrientadorNota, Long> {
    List<OrientadorNota> findByEstudianteIdOrderByCreatedAtDesc(Long estudianteId);
}