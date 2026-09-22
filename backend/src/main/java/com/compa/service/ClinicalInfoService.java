package com.wellness.backend.service;

import com.wellness.backend.dto.request.ClinicalInfoRequest;
import com.wellness.backend.dto.request.HealthStatusUpdateRequest;
import com.wellness.backend.enums.HealthStatus;
import com.wellness.backend.exception.BusinessException;
import com.wellness.backend.exception.ResourceNotFoundException;
import com.wellness.backend.model.ClinicalInfo;
import com.wellness.backend.model.HealthStatusHistory;
import com.wellness.backend.model.Patient;
import com.wellness.backend.repository.ClinicalInfoRepository;
import com.wellness.backend.repository.HealthStatusHistoryRepository;
import com.wellness.backend.repository.PatientRepository;

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
    private final PatientRepository patientRepository;

    public ClinicalInfoService(ClinicalInfoRepository clinicalInfoRepository,
            HealthStatusHistoryRepository historyRepository, PatientRepository patientRepository) {
        this.clinicalInfoRepository = clinicalInfoRepository;
        this.historyRepository = historyRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public ClinicalInfo registerClinicalInfo(Long patientId, ClinicalInfoRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", patientId));
        if (clinicalInfoRepository.existsByPatientId(patientId)) {
            throw new BusinessException("El paciente ya tiene informacion clinica registrada.");
        }
        ClinicalInfo clinicalInfo = new ClinicalInfo();
        clinicalInfo.setPatient(patient);
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
    public ClinicalInfo updateClinicalInfo(Long patientId, ClinicalInfoRequest request) {
        ClinicalInfo clinicalInfo = clinicalInfoRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Informacion clinica para paciente con id " + patientId + " no encontrada"));
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
    public ClinicalInfo updateHealthStatus(Long patientId, HealthStatusUpdateRequest request) {
        ClinicalInfo clinicalInfo = clinicalInfoRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Informacion clinica para paciente con id " + patientId + " no encontrada"));
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
    public ClinicalInfo getClinicalInfo(Long patientId) {
        return clinicalInfoRepository.findByPatientId(patientId).orElseThrow(() -> new ResourceNotFoundException(
                "Informacion clinica para paciente con id " + patientId + " no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<HealthStatusHistory> getStatusHistory(Long patientId) {
        ClinicalInfo clinicalInfo = clinicalInfoRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Informacion clinica para paciente con id " + patientId + " no encontrada"));
        return historyRepository.findByClinicalInfoIdOrderByChangedAtDesc(clinicalInfo.getId());
    }

    public List<HealthStatus> getAvailableHealthStatuses() {
        return List.of(HealthStatus.values());
    }

    @JsonIgnore
    @OneToMany(mappedBy = "clinicalInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HealthStatusHistory> statusHistory = new ArrayList<>();

}
