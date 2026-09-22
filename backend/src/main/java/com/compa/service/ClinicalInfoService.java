package com.compa.service;

import com.compa.dto.request.ClinicalInfoRequest;
import com.compa.dto.request.HealthStatusUpdateRequest;
import com.compa.enums.HealthStatus;
import com.compa.exception.BusinessException;
import com.compa.exception.ResourceNotFoundException;
import com.compa.model.ClinicalInfo;
import com.compa.model.HealthStatusHistory;
import com.compa.model.Estudiante;
import com.compa.repository.ClinicalInfoRepository;
import com.compa.repository.HealthStatusHistoryRepository;
import com.compa.repository.EstudianteRepository;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Service
public class ClinicalInfoService {

    private final ClinicalInfoRepository clinicalInfoRepository;
    private final HealthStatusHistoryRepository historyRepository;
    private final EstudianteRepository estudianteRepository;

    public ClinicalInfoService(ClinicalInfoRepository clinicalInfoRepository,
            HealthStatusHistoryRepository historyRepository, EstudianteRepository estudianteRepository) {
        this.clinicalInfoRepository = clinicalInfoRepository;
        this.historyRepository = historyRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Transactional
    public ClinicalInfo registerClinicalInfo(Long estudianteId, ClinicalInfoRequest request) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", estudianteId));
        if (clinicalInfoRepository.existsByEstudianteId(estudianteId)) {
            throw new BusinessException("El estudiante ya tiene informacion clinica registrada.");
        }
        ClinicalInfo clinicalInfo = new ClinicalInfo();
        clinicalInfo.setEstudiante(estudiante);
        clinicalInfo.setMainCondition(request.getMainCondition());
        clinicalInfo.setSecondaryConditions(request.getSecondaryConditions());
        clinicalInfo.setHealthStatus(request.getHealthStatus());
        clinicalInfo = clinicalInfoRepository.save(clinicalInfo);

        HealthStatusHistory history = new HealthStatusHistory();
        history.setClinicalInfo(clinicalInfo);
        history.setPreviousStatus(null);
        history.setNewStatus(request.getHealthStatus());
        history.setJustification(request.getJustification());
        historyRepository.save(history);

        return clinicalInfo;
    }

    @Transactional
    public ClinicalInfo updateClinicalInfo(Long estudianteId, ClinicalInfoRequest request) {
        ClinicalInfo clinicalInfo = clinicalInfoRepository.findByEstudianteId(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Informacion clinica para estudiante con id " + estudianteId + " no encontrada"));
        HealthStatus previousStatus = clinicalInfo.getHealthStatus();
        clinicalInfo.setMainCondition(request.getMainCondition());
        clinicalInfo.setSecondaryConditions(request.getSecondaryConditions());
        if (!previousStatus.equals(request.getHealthStatus())) {
            clinicalInfo.setHealthStatus(request.getHealthStatus());
            HealthStatusHistory history = new HealthStatusHistory();
            history.setClinicalInfo(clinicalInfo);
            history.setPreviousStatus(previousStatus);
            history.setNewStatus(request.getHealthStatus());
            history.setJustification(request.getJustification());
            historyRepository.save(history);
        }
        return clinicalInfoRepository.save(clinicalInfo);
    }

    @Transactional
    public ClinicalInfo updateHealthStatus(Long estudianteId, HealthStatusUpdateRequest request) {
        ClinicalInfo clinicalInfo = clinicalInfoRepository.findByEstudianteId(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Informacion clinica para estudiante con id " + estudianteId + " no encontrada"));
        HealthStatus previousStatus = clinicalInfo.getHealthStatus();
        if (previousStatus.equals(request.getNewStatus())) {
            throw new BusinessException(
                    "El nuevo estado debe ser diferente al actual: " + previousStatus.getDisplayName());
        }
        HealthStatusHistory history = new HealthStatusHistory();
        history.setClinicalInfo(clinicalInfo);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(request.getNewStatus());
        history.setJustification(request.getJustification());
        historyRepository.save(history);
        clinicalInfo.setHealthStatus(request.getNewStatus());
        return clinicalInfoRepository.save(clinicalInfo);
    }

    @Transactional(readOnly = true)
    public ClinicalInfo getClinicalInfo(Long estudianteId) {
        return clinicalInfoRepository.findByEstudianteId(estudianteId).orElseThrow(() -> new ResourceNotFoundException(
                "Informacion clinica para estudiante con id " + estudianteId + " no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<HealthStatusHistory> getStatusHistory(Long estudianteId) {
        ClinicalInfo clinicalInfo = clinicalInfoRepository.findByEstudianteId(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Informacion clinica para estudiante con id " + estudianteId + " no encontrada"));
        return historyRepository.findByClinicalInfoIdOrderByChangedAtDesc(clinicalInfo.getId());
    }

    public List<HealthStatus> getAvailableHealthStatuses() {
        return List.of(HealthStatus.values());
    }

    @JsonIgnore
    @OneToMany(mappedBy = "clinicalInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HealthStatusHistory> statusHistory = new ArrayList<>();

}
