package com.epicode.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklySalaryDetailDTO {
    private String userName;
    private BigDecimal hourlyWage;
    private int totalWorkedMinutes;
    private BigDecimal baseSalary;
    private boolean weeklyAllowanceEligible;
    private String weeklyAllowanceReason;
    private BigDecimal weeklyAllowance;
    private BigDecimal finalSalary;
    private List<DailyWorkDetailDTO> dailyDetails;
}