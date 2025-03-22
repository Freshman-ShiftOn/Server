package com.example.approvalservice.service;

import com.example.approvalservice.dto.RepeatScheduleRequest;
import com.example.approvalservice.dto.RepeatScheduleUpdateRequest;
import com.example.approvalservice.model.Schedule;

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

    List<Schedule> updateRepeatSchedule(Long scheduleId, RepeatScheduleUpdateRequest request);
    void deleteRepeatSchedule(Long scheduleId, String deleteOption);
}