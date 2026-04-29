package com.wellness.backend.repository;

import com.wellness.backend.enums.PatientStatus;
import com.wellness.backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Spring Data JPA crea la consulta automáticamente
    boolean existsByIdentityDocument(String identityDocument);

    // Verificar si ya existe un paciente con ese email
    boolean existsByEmail(String email);

    Optional<Patient> findByIdentityDocument(String identityDocument);

    // Buscar paciente por token de activación
    Optional<Patient> findByActivationToken(String activationToken);

    // Buscar por nombre (ignorando mayúsculas) o documento
    List<Patient> findByNameContainingIgnoreCaseOrIdentityDocumentContaining(String name, String identityDocument);

    // Filtrar por estado (Activo/Inactivo)
    List<Patient> findByStatus(PatientStatus status);

    // Listar pacientes vinculados a un profesional específico
    List<Patient> findByProfessionalId(Long professionalId);

    Optional<Patient> findByEmail(String email);

    // Filtrar por condición médica
    @Query("SELECT p FROM Patient p JOIN p.clinicalInfo c WHERE LOWER(c.mainCondition) LIKE LOWER(concat('%', :condition, '%'))")
    List<Patient> findByPrimaryCondition(@Param("condition") String condition);
}
