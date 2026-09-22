package com.wellness.backend.enums;

public enum HealthStatus {

    CRITICO, ESTABLE, EN_OBSERVACION, LEVE;

    public String getDisplayName() {
        switch (this) {
        case CRITICO:
            return "Critico";
        case ESTABLE:
            return "Estable";
        case EN_OBSERVACION:
            return "En observacion";
        case LEVE:
            return "Leve";
        default:
            return this.name();
        }
    }
}
