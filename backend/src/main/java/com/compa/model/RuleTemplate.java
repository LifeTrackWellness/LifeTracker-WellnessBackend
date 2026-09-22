
package com.wellness.backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rule_template")
@Data
@AllArgsConstructor
@NoArgsConstructor


public class RuleTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name ="umbral_default",nullable = false)
    private Integer umbralDefault;

    @Column(name ="umbral_min", nullable = false)
    private Integer umbralMin;

    @Column( name = "umbral_max",nullable = false)
    private Integer umbralMax;

}
