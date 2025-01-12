package com.epicode.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;

@Data
public class SalaryRequestDTO {
    private Long branchId; // 매장 ID
    private BigDecimal basicSalary; // 기본 시급
    private String specificDays; // 특정 요일 (쉼표로 구분된 문자열)
    private Time startTime; // 시작 시간
    private Time endTime; // 종료 시간
    private BigDecimal specificSalary; // 특정 시급
    private Boolean weeklyAllowance; // 주휴수당 여부
    private Integer paymentDate; // 급여 지급일
}
