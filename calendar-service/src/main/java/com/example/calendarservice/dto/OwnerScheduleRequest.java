package com.example.calendarservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class OwnerScheduleRequest {
    private Long branchId;
    private List<Long> workerIds;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> workType;
    private Integer inputType;
} 