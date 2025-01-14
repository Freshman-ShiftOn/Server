package com.epicode.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;

@Data
public class SpecificTimeSalaryRequestDTO {
    private String specificDays;
    private Time startTime;
    private Time endTime;
    private BigDecimal specificSalary;
}
