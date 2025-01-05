package com.example.calendarservice.service;

import com.example.calendarservice.model.Schedule;

import java.util.List;

public interface ScheduleService {
    Schedule createSchedule(Schedule schedule);
    Schedule updateSchedule(Integer scheduleId, Schedule schedule);
    void deleteSchedule(Integer scheduleId);
    List<Schedule> getSchedulesByBranchId(Integer branchId, Integer month);
    List<Schedule> getSchedulesByBranchIdAndUserId(Integer branchId, Integer month, Integer userId);
}