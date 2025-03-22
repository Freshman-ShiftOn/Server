package com.example.calendarservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RepeatScheduleUpdateRequest {
    private String updateOption; // "ONE", "AFTER", "ALL"
    private List<String> workType;
    private Date startTime;
    private Date endTime;
}