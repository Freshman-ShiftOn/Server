package com.epicode.dto;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Time;

@Data
@Builder
public class SpecificTimeSalaryResponseDTO {
    private String branchName;
    private String specificDays;
    private Time startTime;
    private Time endTime;
    private BigDecimal specificSalary;
}