package com.epicode.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SalaryRequestDTO {
    private Long branchId;
    private BigDecimal basicSalary;
    private Boolean weeklyAllowance;
    private Integer paymentDate;
    private List<SpecificTimeSalaryRequestDTO> specificTimeSalaries;
}