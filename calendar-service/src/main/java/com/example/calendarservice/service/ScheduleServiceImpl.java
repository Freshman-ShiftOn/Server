package com.example.calendarservice.service;

import com.example.calendarservice.exception.ResourceNotFoundException;
import com.example.calendarservice.model.Schedule;
import com.example.calendarservice.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Override
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule updateSchedule(Integer scheduleId, Schedule schedule) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));

        existingSchedule.setBranchId(schedule.getBranchId());
        existingSchedule.setWorkerId(schedule.getWorkerId());
        existingSchedule.setWorkType(schedule.getWorkType());
        existingSchedule.setStartTime(schedule.getStartTime());
        existingSchedule.setEndTime(schedule.getEndTime());

        return scheduleRepository.save(existingSchedule);
    }

    @Override
    public void deleteSchedule(Integer scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException("Schedule not found with id " + scheduleId);
        }
        scheduleRepository.deleteById(scheduleId);
    }

    @Override
    public List<Schedule> getSchedulesByBranchId(Integer branchId, Integer month) {
        return scheduleRepository.findByBranchIdAndMonth(branchId, month);
    }

    @Override
    public List<Schedule> getSchedulesByBranchIdAndUserId(Integer branchId, Integer month, Integer userId) {
        return scheduleRepository.findByBranchIdAndMonthAndUserId(branchId, month, userId);
    }

    public boolean isUserSchedule(Integer scheduleId, int userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
        return schedule.getWorkerId().equals(userId);
    }

    public boolean isScheduleInBranch(Integer scheduleId, Integer branchId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));
        return schedule.getBranchId().equals(branchId);
    }
}
