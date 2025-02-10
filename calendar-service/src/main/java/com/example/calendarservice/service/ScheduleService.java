package com.example.calendarservice.service;

import com.example.calendarservice.dto.RepeatScheduleRequest;
import com.example.calendarservice.model.Schedule;

import java.util.List;

public interface ScheduleService {
    Schedule createSchedule(Schedule schedule);
    Schedule updateSchedule(Long scheduleId, Schedule schedule);
    void deleteSchedule(Long scheduleId);
    List<Schedule> getSchedulesByBranchId(Long branchId, Integer month);
    List<Schedule> getSchedulesByBranchIdAndUserId(Long branchId, Integer month, Long userId);
    boolean isUserSchedule(Long scheduleId, Long userId);
    boolean isScheduleInBranch(Long scheduleId, Long branchId);
    List<Schedule> createRepeatSchedules(RepeatScheduleRequest repeatRequest);
}