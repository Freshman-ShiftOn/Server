package com.example.calendarservice.service;

import com.example.calendarservice.dto.RepeatScheduleRequest;
import com.example.calendarservice.exception.ResourceNotFoundException;
import com.example.calendarservice.model.Schedule;
import com.example.calendarservice.repository.ScheduleRepository;
import com.example.calendarservice.repository.ShiftRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ShiftRequestRepository shiftRequestRepository;

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
        // 외래 키 오류 방지를 위해 먼저 ShiftRequest 삭제
        shiftRequestRepository.deleteByScheduleId(scheduleId);
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

        LocalDate startDate = repeatRequest.getStartDate();
        LocalDate endDate = repeatRequest.getEndDate();
        LocalTime startTime = repeatRequest.getStartTime();
        LocalTime endTime = repeatRequest.getEndTime();

        // 첫 번째 일정 생성 (repeatGroupId 없이)
        Schedule firstSchedule = createScheduleFromRepeatRequest(repeatRequest, startDate, startTime, endTime, null);
        firstSchedule = scheduleRepository.save(firstSchedule);  // DB에 저장하여 ID 생성

        // 첫 번째 스케줄 ID를 repeatGroupId로 설정하여 나머지 일정 생성
        Long repeatGroupId = firstSchedule.getId();
        firstSchedule.setRepeatGroupId(repeatGroupId);
        scheduleRepository.save(firstSchedule);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (repeatRequest.getDaysOfWeek().contains(date.getDayOfWeek())) {
                schedules.add(createScheduleFromRepeatRequest(repeatRequest, date, startTime, endTime, repeatGroupId));
            }
        }

        // 스케줄 저장
        return scheduleRepository.saveAll(schedules);
    }

    private Schedule createScheduleFromRepeatRequest(RepeatScheduleRequest repeatRequest, LocalDate date, LocalTime startTime, LocalTime endTime, Long repeatGroupId) {
        return Schedule.builder()
                .branchId(repeatRequest.getBranchId())
                .workerId(repeatRequest.getWorkerId())
                .workerName(repeatRequest.getWorkerName())
                .workType(repeatRequest.getWorkType())
                .inputType(2) // 반복근무
                .repeatGroupId(repeatGroupId)
                .startTime(convertToDate(date.atTime(startTime)))
                .endTime(convertToDate(date.atTime(endTime)))
                .lastUpdated(new Date())
                .build();
    }

    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    @Transactional
    public List<Schedule> updateRepeatSchedule(Long scheduleId, RepeatScheduleRequest repeatRequest) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));

        Long repeatGroupId = existingSchedule.getRepeatGroupId();

        // 기존 스케줄 조회
        List<Schedule> existingSchedules = scheduleRepository.findByRepeatGroupId(repeatGroupId);

        // ShiftRequest 먼저 삭제
        List<Long> scheduleIdsToDelete = existingSchedules.stream()
                .map(Schedule::getId)
                .toList();
        shiftRequestRepository.deleteByScheduleIdIn(scheduleIdsToDelete);

        // 기존 스케줄 삭제
        scheduleRepository.deleteAll(existingSchedules);

        // 새 스케줄 생성
        List<Schedule> newSchedules = new ArrayList<>();
        LocalDate startDate = repeatRequest.getStartDate();
        LocalDate endDate = repeatRequest.getEndDate();
        LocalTime startTime = repeatRequest.getStartTime();
        LocalTime endTime = repeatRequest.getEndTime();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (repeatRequest.getDaysOfWeek().contains(date.getDayOfWeek())) {
                newSchedules.add(createScheduleFromRepeatRequest(repeatRequest, date, startTime, endTime, repeatGroupId));
            }
        }

        return scheduleRepository.saveAll(newSchedules);
    }

    @Transactional
    public void deleteRepeatSchedule(Long scheduleId, String deleteOption) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));

        switch (deleteOption) {
            case "ONE":
                // 외래 키 오류 방지를 위해 먼저 ShiftRequest 삭제
                shiftRequestRepository.deleteByScheduleId(scheduleId);
                // 이 일정만 삭제
                scheduleRepository.deleteById(scheduleId);
                break;

            case "AFTER":
                // 해당 날짜 이후(포함) 모든 일정 삭제
                List<Schedule> schedulesToDelete = scheduleRepository.findByRepeatGroupIdAndStartTimeGreaterThanEqual(
                        existingSchedule.getRepeatGroupId(), existingSchedule.getStartTime());

                // 외래 키 오류 방지를 위해 먼저 ShiftRequest 삭제
                List<Long> scheduleIdsToDelete = schedulesToDelete.stream().map(Schedule::getId).toList();
                shiftRequestRepository.deleteByScheduleIdIn(scheduleIdsToDelete);

                scheduleRepository.deleteAll(schedulesToDelete);
                break;

            case "ALL":
                // 전체 반복 일정 삭제
                List<Schedule> allSchedules = scheduleRepository.findByRepeatGroupId(existingSchedule.getRepeatGroupId());

                // 외래 키 오류 방지를 위해 먼저 ShiftRequest 삭제
                List<Long> allScheduleIds = allSchedules.stream().map(Schedule::getId).toList();
                shiftRequestRepository.deleteByScheduleIdIn(allScheduleIds);

                scheduleRepository.deleteAll(allSchedules);
                break;

            default:
                throw new IllegalArgumentException("Invalid delete option: " + deleteOption);
        }
    }
}
