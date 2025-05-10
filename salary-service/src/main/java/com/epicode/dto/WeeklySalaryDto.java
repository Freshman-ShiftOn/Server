package com.epicode.dto;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySalaryDto {
    private Long userId;
    private String name;
    private Long branchId;
    private Integer week;
    private Integer totalMinutes;
    private String personal_cost;
    private BigDecimal calculatedSalary;
    private Boolean weeklyAllowanceEligible;
}