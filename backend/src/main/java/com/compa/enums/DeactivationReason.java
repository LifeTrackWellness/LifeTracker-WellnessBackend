package com.wellness.backend.enums;

public enum DeactivationReason {
    ALTA_MEDICA, ABANDONO_PERDIDA_SEGUIMIENTO;

    public String getDisplayName() {
        switch (this) {
        case ALTA_MEDICA:
            return "Alta medica";
        case ABANDONO_PERDIDA_SEGUIMIENTO:
            return "Abandono/Perdida de seguimiento";
        default:
            return this.name();
        }
    }

}
