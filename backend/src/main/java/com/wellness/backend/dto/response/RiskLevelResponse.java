package com.wellness.backend.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.wellness.backend.enums.RiskLevel;

public class RiskLevelResponse {
    private Long patientId;
    private String patientName;
    private RiskLevel riskLevel;
    private String riskLevelDisplay;
    private String riskLevelDescription;
    private double compliancePercentage;
    private LocalDate evaluatedDate;
    private LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RiskLevelResponse obj = new RiskLevelResponse();

        public Builder patientId(Long v) {
            obj.patientId = v;
            return this;
        }

        public Builder patientName(String v) {
            obj.patientName = v;
            return this;
        }

        public Builder riskLevel(RiskLevel v) {
            obj.riskLevel = v;
            return this;
        }

        public Builder riskLevelDisplay(String v) {
            obj.riskLevelDisplay = v;
            return this;
        }

        public Builder riskLevelDescription(String v) {
            obj.riskLevelDescription = v;
            return this;
        }

        public Builder compliancePercentage(double v) {
            obj.compliancePercentage = v;
            return this;
        }

        public Builder evaluatedDate(LocalDate v) {
            obj.evaluatedDate = v;
            return this;
        }

        public Builder createdAt(LocalDateTime v) {
            obj.createdAt = v;
            return this;
        }

        public RiskLevelResponse build() {
            return obj;
        }
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getRiskLevelDisplay() {
        return riskLevelDisplay;
    }

    public String getRiskLevelDescription() {
        return riskLevelDescription;
    }

    public double getCompliancePercentage() {
        return compliancePercentage;
    }

    public LocalDate getEvaluatedDate() {
        return evaluatedDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
