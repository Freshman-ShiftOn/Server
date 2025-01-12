package com.epicode.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;

@Data
public class SalaryResponseDTO {
    private Long id; // Salary ID
    private Long userId; // 사용자 ID
    private Long branchId; // Branch ID
    private BigDecimal basicSalary; // 기본 시급
    private String specificDays; // 특정 요일
    private Time startTime; // 시작 시간
    private Time endTime; // 종료 시간
    private BigDecimal specificSalary; // 특정 시급
    private Boolean weeklyAllowance; // 주휴수당 여부
}