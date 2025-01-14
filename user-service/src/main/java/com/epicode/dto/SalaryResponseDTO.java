package com.epicode.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class SalaryResponseDTO {
    private Long userId;
    private Long branchId;
    private String branchName;
    private BigDecimal basicSalary;
    private Boolean weeklyAllowance;
    private List<SpecificTimeSalaryResponseDTO> specificTimeSalaries;
}