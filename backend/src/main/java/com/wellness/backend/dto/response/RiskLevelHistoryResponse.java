package com.wellness.backend.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.wellness.backend.enums.RiskLevel;

public class RiskLevelHistoryResponse {
    private Long id;
    private RiskLevel riskLevel;
    private String riskLevelDisplay;
    private RiskLevel previousRiskLevel;
    private String previousRiskLevelDisplay;
    private double compliancePercentage;
    private LocalDate evaluatedDate;
    private LocalDateTime createdAt;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RiskLevelHistoryResponse obj = new RiskLevelHistoryResponse();

        public Builder id(Long v) {
            obj.id = v;
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

        public Builder previousRiskLevel(RiskLevel v) {
            obj.previousRiskLevel = v;
            return this;
        }

        public Builder previousRiskLevelDisplay(String v) {
            obj.previousRiskLevelDisplay = v;
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

        public RiskLevelHistoryResponse build() {
            return obj;
        }
    }

    public Long getId() {
        return id;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getRiskLevelDisplay() {
        return riskLevelDisplay;
    }

    public RiskLevel getPreviousRiskLevel() {
        return previousRiskLevel;
    }

    public String getPreviousRiskLevelDisplay() {
        return previousRiskLevelDisplay;
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
