package com.epicode.dto;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
public class WeeklySalaryDto {
    private Long userId;
    private String name;
    private Long branchId;
    private Integer totalMinutes;
    private BigDecimal personalCost;
    private BigDecimal calculatedSalary;
    private Boolean weeklyAllowanceEligible;
}