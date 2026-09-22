package com.compa.enums;

public enum TaskPriority
{
    ALTA, MEDIA, BAJA;

    public String getDisplayName() {
        switch (this) {
            case ALTA: return "Alta";
            case MEDIA: return "Media";
            case BAJA: return "Baja";
            default: return this.name();
        }
    }
}
