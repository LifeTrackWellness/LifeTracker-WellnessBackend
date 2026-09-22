package com.compa.dto.response;

import com.compa.enums.HealthStatus;
import com.compa.enums.PlanStatus;
import com.compa.enums.RiskLevel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class ProgressReportResponse
{
    // Encabezado
    private String estudianteFullName;
    private String orientadorFullName;
    private LocalDateTime generatedAt;

    // Métricas de adherencia
    private Double weeklyCompliance;
    private Integer currentStreak;
    private Integer bestStreak;

    // Niveles de riesgo
    private List<RiskDayDTO> last7DaysRisk;
    private Long highRiskCount;

    // Evolución de salud
    private HealthStatus initialHealthStatus;
    private HealthStatus currentHealthStatus;
    private LocalDateTime initialStatusDate;
    private LocalDateTime currentStatusDate;

    // Planes de hábitos
    private List<HabitPlanDTO> habitPlans;

    // Conclusiones
    private List<ConclusionDTO> conclusions;

    // DTOs internos
    @Data
    @Builder
    public static class RiskDayDTO {
        private LocalDate date;
        private RiskLevel riskLevel;
        private Double compliancePercentage;
    }

    @Data
    @Builder
    public static class HabitPlanDTO {
        private String name;
        private LocalDate startDate;
        private PlanStatus status;
    }

    @Data
    @Builder
    public static class ConclusionDTO {
        private Long id;
        private String content;
        private String orientadorName;
        private LocalDateTime createdAt;
    }
}
