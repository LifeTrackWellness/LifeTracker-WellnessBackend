package com.compa.repository;

import com.compa.model.ConsentTemplate;
import com.compa.model.Estudiante;
import com.compa.model.EstudianteConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteConsentRepository extends JpaRepository<EstudianteConsent, Long>
{
    List<EstudianteConsent> findByEstudiante(Estudiante estudiante);
    List<EstudianteConsent> findByEstudianteAndAceptado(Estudiante estudiante, boolean aceptado);
}
