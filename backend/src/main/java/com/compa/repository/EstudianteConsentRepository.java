package com.wellness.backend.repository;

import com.wellness.backend.model.ConsentTemplate;
import com.wellness.backend.model.Patient;
import com.wellness.backend.model.PatientConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientConsentRepository extends JpaRepository<PatientConsent, Long>
{
    List<PatientConsent> findByPatient(Patient patient);
    List<PatientConsent> findByPatientAndAceptado(Patient patient, boolean aceptado);
}
