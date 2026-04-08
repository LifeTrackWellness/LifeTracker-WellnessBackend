package com.wellness.backend.enums;

public enum EmotionalState {
    MUY_MAL, MAL, REGULAR, BIEN, MUY_BIEN;

    public String getDisplayName() {
        switch (this) {
            case MUY_MAL:
                return "Muy mal";
            case MAL:
                return "Mal";
            case REGULAR:
                return "Regular";
            case BIEN:
                return "Bien";
            case MUY_BIEN:
                return "Muy bien";
            default:
                return this.name();
        }
    }

    public String getIcon() {
        switch (this) {
            case MUY_MAL:
                return "😢";
            case MAL:
                return "😕";
            case REGULAR:
                return "😐";
            case BIEN:
                return "🙂";
            case MUY_BIEN:
                return "😄";
            default:
                return "😐";
        }
    }

}
