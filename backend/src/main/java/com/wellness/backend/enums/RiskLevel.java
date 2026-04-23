package com.wellness.backend.enums;

public enum RiskLevel {
    VERDE, AMARILLO, ROJO;

    public String getDisplayName() {
        switch (this) {
            case VERDE:
                return "Verde";
            case AMARILLO:
                return "Amarillo";
            case ROJO:
                return "Rojo";
            default:
                return this.name();
        }
    }

    public String getDescription() {
        switch (this) {
            case VERDE:
                return "Cumplimiento >= 80%";
            case AMARILLO:
                return "Cumplimiento entre 50% y 79%";
            case ROJO:
                return "Cumplimiento < 50%";
            default:
                return "";
        }
    }
}