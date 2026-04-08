package com.wellness.backend.enums;

public enum TaskBarrier {
    FALTA_DE_TIEMPO,
    OLVIDO,
    NO_QUISE,
    ME_SENTI_MAL,
    OTRO;

    public String getDisplayName() {
        switch (this) {
            case FALTA_DE_TIEMPO:
                return "Falta de tiempo";
            case OLVIDO:
                return "Olvido";
            case NO_QUISE:
                return "No quise";
            case ME_SENTI_MAL:
                return "Me senti mal";
            case OTRO:
                return "Otro";
            default:
                return this.name();
        }
    }
}
