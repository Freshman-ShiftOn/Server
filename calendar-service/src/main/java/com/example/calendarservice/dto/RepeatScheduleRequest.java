package com.example.calendarservice.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
public class RepeatScheduleRequest {
    private Long branchId;
    private Long workerId;
    private String workerName;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private List<DayOfWeek> daysOfWeek;
    private List<String> workType;
}
