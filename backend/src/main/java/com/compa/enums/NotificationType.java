package com.compa.enums;

public enum NotificationType {
    MENSAJE_MOTIVACIONAL,
    RECORDATORIO_CHECKIN,
    RACHA_EN_RIESGO,
    MENSAJE_ORIENTADOR;

    public String getDisplayName() {
        switch (this) {
            case MENSAJE_MOTIVACIONAL:
                return "Mensaje motivacional";
            case RECORDATORIO_CHECKIN:
                return "Recordatorio de check-in";
            case RACHA_EN_RIESGO:
                return "Racha en riesgo";
            case MENSAJE_ORIENTADOR:
                return "Mensaje del orientador";
            default:
                return this.name();
        }
    }
}
