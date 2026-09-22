package com.wellness.backend.dto.response;

import com.wellness.backend.enums.EmotionalState;

import java.time.LocalDate;

public class CheckInSummaryResponse {

    private LocalDate date;
    private String status; // "COMPLETADO", "NO_REGISTRADO"
    private EmotionalState emotionalState;
    private String emotionalStateIcon;
    private Long checkInId;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final CheckInSummaryResponse obj = new CheckInSummaryResponse();
        public Builder date(LocalDate v) { obj.date = v; return this; }
        public Builder status(String v) { obj.status = v; return this; }
        public Builder emotionalState(EmotionalState v) { obj.emotionalState = v; return this; }
        public Builder emotionalStateIcon(String v) { obj.emotionalStateIcon = v; return this; }
        public Builder checkInId(Long v) { obj.checkInId = v; return this; }
        public CheckInSummaryResponse build() { return obj; }
    }

    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public EmotionalState getEmotionalState() { return emotionalState; }
    public String getEmotionalStateIcon() { return emotionalStateIcon; }
    public Long getCheckInId() { return checkInId; }
}
    

