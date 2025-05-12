package com.epicode.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyWorkDetailDTO {
    private LocalDate workDate;       // 날짜 (2025-04-01 등)
    private String dayOfWeek;         // 요일 (화, 목 등)
    private String workTime;          // 근무 시간 (08:00 - 15:00)
    private int workedMinutes;        // 근무 길이 (단위: 분) → 예: 420
    private List<String> workType;          // 근무 유형 (오픈, 마감, 미들)
}