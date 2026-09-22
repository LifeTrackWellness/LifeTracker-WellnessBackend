package com.wellness.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "adherence_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class AdherenceSnapshot
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="patient_id",nullable = false)
    @JsonIgnore
    private Patient patient;

    @Column(name="snapshot_date",nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "weekly_compliance")
    private Double weeklyCompliance;


    @Column(name= "monthly_compliance")
    private Double monthlyCompliance;


    @Column(name="current_streak")
    private Integer currentStreak;

    @Column(name =" consistency")
    private Double consistency;

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @PrePersist
    public void onCreate()
    {
        this.createdAt = LocalDate.now();
    }




}
