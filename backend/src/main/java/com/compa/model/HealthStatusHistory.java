package com.wellness.backend.model;

import com.wellness.backend.enums.HealthStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "health_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_info_id", nullable = false)
    private ClinicalInfo clinicalInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private HealthStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private HealthStatus newStatus;

    @Column(name = "justification", nullable = false, columnDefinition = "TEXT")
    private String justification;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_info_id", nullable = false, insertable = false, updatable = false)
    private ClinicalInfo clinicalInfoRef;
}
