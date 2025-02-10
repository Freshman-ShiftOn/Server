package com.example.calendarservice.service;

import com.example.calendarservice.dto.RepeatScheduleRequest;
import com.example.calendarservice.exception.ResourceNotFoundException;
import com.example.calendarservice.model.Schedule;
import com.example.calendarservice.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
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
    public Schedule updateSchedule(Long scheduleId, Schedule schedule) {
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
    public void deleteSchedule(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException("Schedule not found with id " + scheduleId);
        }
        scheduleRepository.deleteById(scheduleId);
    }

    @Override
    public List<Schedule> getSchedulesByBranchId(Long branchId, Integer month) {
        return scheduleRepository.findByBranchIdAndMonth(branchId, month);
    }

    @Override
    public List<Schedule> getSchedulesByBranchIdAndUserId(Long branchId, Integer month, Long userId) {
        return scheduleRepository.findByBranchIdAndMonthAndUserId(branchId, month, userId);
    }

    @Override
    public boolean isUserSchedule(Long scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
        return schedule.getWorkerId().equals(userId);
    }

    @Override
    public boolean isScheduleInBranch(Long scheduleId, Long branchId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));
        return schedule.getBranchId().equals(branchId);
    }

    @Override
    public List<Schedule> createRepeatSchedules(RepeatScheduleRequest repeatRequest) {
        List<Schedule> schedules = new ArrayList<>();

        // 날짜와 반복 로직 처리
        LocalDate startDate = repeatRequest.getRepeat().getStartDate();
        LocalDate endDate = repeatRequest.getRepeat().getEndDate();
        LocalTime startTime = repeatRequest.getStartTime();
        LocalTime endTime = repeatRequest.getEndTime();

        switch (repeatRequest.getRepeat().getType()) {
            case "DAILY":
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    schedules.add(createScheduleFromRepeatRequest(repeatRequest, date, startTime, endTime));
                }
                break;

            case "WEEKLY":
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusWeeks(1)) {
                    schedules.add(createScheduleFromRepeatRequest(repeatRequest, date, startTime, endTime));
                }
                break;

            case "CUSTOM":
                List<DayOfWeek> daysOfWeek = repeatRequest.getRepeat().getDaysOfWeek();
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    if (daysOfWeek.contains(date.getDayOfWeek())) {
                        schedules.add(createScheduleFromRepeatRequest(repeatRequest, date, startTime, endTime));
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid repeat type: " + repeatRequest.getRepeat().getType());
        }

        // 스케줄 저장
        return scheduleRepository.saveAll(schedules);
    }

    private Schedule createScheduleFromRepeatRequest(RepeatScheduleRequest request, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return Schedule.builder()
                .branchId(request.getBranchId())
                .workerId(request.getUserId())
                .workType(request.getWorkType())
                .inputType(2) // 반복근무
                .startTime(convertToDate(date.atTime(startTime)))
                .endTime(convertToDate(date.atTime(endTime)))
                .lastUpdated(new Date())
                .build();
    }

    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
