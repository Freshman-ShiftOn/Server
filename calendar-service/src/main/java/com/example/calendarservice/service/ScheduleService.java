package com.example.calendarservice.service;

import com.example.calendarservice.dto.OwnerRepeatScheduleRequest;
import com.example.calendarservice.dto.OwnerScheduleRequest;
import com.example.calendarservice.dto.RepeatScheduleRequest;
import com.example.calendarservice.dto.RepeatScheduleUpdateRequest;
import com.example.calendarservice.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    Schedule createSchedule(Schedule schedule);
    Schedule updateSchedule(Long scheduleId, Schedule schedule);
    void deleteSchedule(Long scheduleId);
    List<Schedule> getSchedulesByBranchId(Long branchId, Integer month);
    List<Schedule> getSchedulesByBranchIdAndUserId(Long branchId, Integer month, Long userId);
    List<Schedule> getSchedulesByBranchIdAndDateRange(Long branchId, LocalDate startDate, LocalDate endDate);
    List<Schedule> getSchedulesByBranchIdAndUserIdAndDateRange(Long branchId, Long userId, LocalDate startDate, LocalDate endDate);
    boolean isUserSchedule(Long scheduleId, Long userId);
    boolean isScheduleInBranch(Long scheduleId, Long branchId);
    List<Schedule> createRepeatSchedules(RepeatScheduleRequest repeatRequest);
    List<Schedule> createSchedulesForWorkers(OwnerScheduleRequest request);
    List<Schedule> createRepeatSchedulesForWorkers(OwnerRepeatScheduleRequest request);
    List<Schedule> updateRepeatSchedule(Long scheduleId, RepeatScheduleUpdateRequest request);
    void deleteRepeatSchedule(Long scheduleId, String deleteOption);
}