package com.epicode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyBranchUserSummaryDTO {
    private Long userId;
    private String userName;
    private BigDecimal hourlyWage;
    private Integer totalWorkedMinutes;
    private BigDecimal totalSalary;
    private BigDecimal weeklyAllowance;
    private BigDecimal finalSalary;
}
