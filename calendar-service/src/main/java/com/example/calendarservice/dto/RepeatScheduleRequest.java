package com.example.calendarservice.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class RepeatScheduleRequest {
    private Long branchId;
    private Long workerId;
    private String workerName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Repeat repeat;
    private List<String> workType;

    @Data
    public static class Repeat {
        private String type; // DAILY, WEEKLY, CUSTOM
        private List<DayOfWeek> daysOfWeek; // CUSTOM에서 사용
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
