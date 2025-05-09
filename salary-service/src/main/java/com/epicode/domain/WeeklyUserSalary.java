package com.epicode.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "weekly_user_salary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "branch_id", "year", "month", "week"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyUserSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "week", nullable = false)
    private Integer week;

    @Column(name = "total_minutes", nullable = false)
    private Integer totalMinutes;

    @Column(name = "calculated_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal calculatedSalary;

    @Column(name = "weekly_allowance_eligible", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean weeklyAllowanceEligible;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}