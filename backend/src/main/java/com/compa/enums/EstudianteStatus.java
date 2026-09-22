package com.wellness.backend.enums;

public enum PatientStatus {
    ACTIVO, INACTIVO;

    public String getDisplayName() {
        switch (this) {
        case ACTIVO:
            return "Activo";
        case INACTIVO:
            return "Inactivo";
        default:
            return this.name();
        }
    }

}
