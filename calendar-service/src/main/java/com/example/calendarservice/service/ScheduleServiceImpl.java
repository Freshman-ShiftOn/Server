package com.example.calendarservice.service;

import com.example.calendarservice.dto.OwnerRepeatScheduleRequest;
import com.example.calendarservice.dto.OwnerScheduleRequest;
import com.example.calendarservice.dto.RepeatScheduleRequest;
import com.example.calendarservice.dto.RepeatScheduleUpdateRequest;
import com.example.calendarservice.exception.ResourceNotFoundException;
import com.example.calendarservice.exception.ScheduleConflictException;
import com.example.calendarservice.message.ScheduleEventProducer;
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
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ShiftRequestRepository shiftRequestRepository;
    private final ScheduleEventProducer scheduleEventProducer;
    private final UserService userService;

    @Override
    public Schedule createSchedule(Schedule schedule) {
        if (scheduleRepository.existsOverlappingSchedule(
                schedule.getWorkerId(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                null)) {
            throw new ScheduleConflictException("해당 시간에 이미 다른 스케줄이 존재합니다.");
        }
        Schedule saved = scheduleRepository.save(schedule);
        scheduleEventProducer.sendScheduleWorkedEvent(saved);
        return saved;
    }

    @Override
    public Schedule updateSchedule(Long scheduleId, Schedule schedule) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));

        // 시간이 변경되는 경우에만 중복 체크
        if (!existingSchedule.getStartTime().equals(schedule.getStartTime()) ||
            !existingSchedule.getEndTime().equals(schedule.getEndTime())) {
            // 자기 자신을 제외한 중복 체크
            if (scheduleRepository.existsOverlappingSchedule(
                    schedule.getWorkerId(), 
                    schedule.getStartTime(), 
                    schedule.getEndTime(),
                    scheduleId)) { // scheduleId를 제외하고 검사
                throw new ScheduleConflictException("해당 시간에 이미 다른 스케줄이 존재합니다.");
            }
        }

        boolean timeChanged =
                !existingSchedule.getStartTime().equals(schedule.getStartTime()) ||
                        !existingSchedule.getEndTime().equals(schedule.getEndTime());
        
        existingSchedule.setBranchId(schedule.getBranchId());
        existingSchedule.setWorkerId(schedule.getWorkerId());
        existingSchedule.setWorkType(schedule.getWorkType());
        existingSchedule.setStartTime(schedule.getStartTime());
        existingSchedule.setEndTime(schedule.getEndTime());

        Schedule updated = scheduleRepository.save(existingSchedule);

        if (timeChanged) {
            scheduleEventProducer.sendScheduleWorkedEvent(updated);
        }

        return updated;
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException("Schedule not found with id " + scheduleId);
        }
        Schedule deletedSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));;

        // 외래 키 오류 방지를 위해 먼저 ShiftRequest 삭제
        shiftRequestRepository.deleteByScheduleId(scheduleId);
        scheduleRepository.deleteById(scheduleId);
        scheduleEventProducer.sendScheduleDeletedEvent(deletedSchedule);
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

        // 첫 번째 일정 생성 (repeatGroupId 없이)
        Schedule firstSchedule = createScheduleFromRepeatRequest(repeatRequest, startDate, startTime, endTime, null);
        firstSchedule = scheduleRepository.save(firstSchedule);  // DB에 저장하여 ID 생성

        // 첫 번째 스케줄 ID를 repeatGroupId로 설정하여 나머지 일정 생성
        Long repeatGroupId = firstSchedule.getId();
        firstSchedule.setRepeatGroupId(repeatGroupId);
        scheduleRepository.save(firstSchedule);

        // 나머지 일정 생성
        switch (repeatRequest.getRepeat().getType()) {
            case "DAILY":
                for (LocalDate date = startDate.plusDays(1); !date.isAfter(endDate); date = date.plusDays(1)) {
                    schedules.add(createScheduleFromRepeatRequest(repeatRequest, date, startTime, endTime, repeatGroupId));
                }
                break;

            case "WEEKLY":
                for (LocalDate date = startDate.plusDays(1); !date.isAfter(endDate); date = date.plusWeeks(1)) {
                    schedules.add(createScheduleFromRepeatRequest(repeatRequest, date, startTime, endTime, repeatGroupId));
                }
                break;

            case "CUSTOM":
                List<DayOfWeek> daysOfWeek = repeatRequest.getRepeat().getDaysOfWeek();
                for (LocalDate date = startDate.plusDays(1); !date.isAfter(endDate); date = date.plusDays(1)) {
                    if (daysOfWeek.contains(date.getDayOfWeek())) {
                        schedules.add(createScheduleFromRepeatRequest(repeatRequest, date, startTime, endTime, repeatGroupId));
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid repeat type: " + repeatRequest.getRepeat().getType());
        }
        List<Schedule> saved = scheduleRepository.saveAll(schedules);
        scheduleEventProducer.sendScheduleWorkedEvent(firstSchedule);
        scheduleEventProducer.sendSchedules(saved);
        //스케줄 저장
        return saved;
    }

    private Schedule createScheduleFromRepeatRequest(RepeatScheduleRequest request, LocalDate date, LocalTime startTime, LocalTime endTime, Long repeatGroupId) {
        return Schedule.builder()
                .branchId(request.getBranchId())
                .workerId(request.getWorkerId())
                .workerName(request.getWorkerName())
                .workType(request.getWorkType())
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
    public List<Schedule> updateRepeatSchedule(Long scheduleId, RepeatScheduleUpdateRequest request) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));

        List<Schedule> schedulesToUpdate = new ArrayList<>();

        switch (request.getUpdateOption()) {
            case "ONE":
                // 이 일정만 수정 (독립적으로 변경)
                if (scheduleRepository.existsOverlappingSchedule(
                        existingSchedule.getWorkerId(),
                        request.getStartTime(),
                        request.getEndTime(),
                        scheduleId)) {
                    throw new ScheduleConflictException("해당 시간에 이미 다른 스케줄이 존재합니다.");
                }
                
                existingSchedule.setWorkType(request.getWorkType());
                existingSchedule.setStartTime(request.getStartTime());
                existingSchedule.setEndTime(request.getEndTime());
                existingSchedule.setInputType(1); // 단일 일정으로 변경
                schedulesToUpdate.add(scheduleRepository.save(existingSchedule));
                break;

            case "AFTER":
                // 해당 날짜 이후(포함) 모든 일정 수정
                schedulesToUpdate = scheduleRepository.findByRepeatGroupIdAndStartTimeGreaterThanEqual(
                        existingSchedule.getRepeatGroupId(), existingSchedule.getStartTime());
                
                // 모든 수정될 일정에 대해 중복 체크
                for (Schedule schedule : schedulesToUpdate) {
                    if (scheduleRepository.existsOverlappingSchedule(
                            schedule.getWorkerId(),
                            request.getStartTime(),
                            request.getEndTime(),
                            schedule.getId())) {
                        throw new ScheduleConflictException(
                                String.format("스케줄 ID %d: 해당 시간에 이미 다른 스케줄이 존재합니다.", schedule.getId()));
                    }
                }

                // 중복 체크 통과 후 일정 수정
                for (Schedule schedule : schedulesToUpdate) {
                    schedule.setWorkType(request.getWorkType());
                    schedule.setStartTime(request.getStartTime());
                    schedule.setEndTime(request.getEndTime());
                }
                schedulesToUpdate = scheduleRepository.saveAll(schedulesToUpdate);
                break;

            case "ALL":
                // 전체 반복 일정 수정
                schedulesToUpdate = scheduleRepository.findByRepeatGroupId(existingSchedule.getRepeatGroupId());
                
                // 모든 수정될 일정에 대해 중복 체크
                for (Schedule schedule : schedulesToUpdate) {
                    if (scheduleRepository.existsOverlappingSchedule(
                            schedule.getWorkerId(),
                            request.getStartTime(),
                            request.getEndTime(),
                            schedule.getId())) {
                        throw new ScheduleConflictException(
                                String.format("스케줄 ID %d: 해당 시간에 이미 다른 스케줄이 존재합니다.", schedule.getId()));
                    }
                }

                // 중복 체크 통과 후 일정 수정
                for (Schedule schedule : schedulesToUpdate) {
                    schedule.setWorkType(request.getWorkType());
                    schedule.setStartTime(request.getStartTime());
                    schedule.setEndTime(request.getEndTime());
                }
                schedulesToUpdate = scheduleRepository.saveAll(schedulesToUpdate);
                break;

            default:
                throw new IllegalArgumentException("Invalid update option: " + request.getUpdateOption());
        }

        return schedulesToUpdate;
    }

    @Transactional
    public void deleteRepeatSchedule(Long scheduleId, String deleteOption) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + scheduleId));

        switch (deleteOption) {
            case "ONE":
                scheduleEventProducer.sendScheduleDeletedEvent(existingSchedule);
                // 외래 키 오류 방지를 위해 먼저 ShiftRequest 삭제
                shiftRequestRepository.deleteByScheduleId(scheduleId);
                // 이 일정만 삭제
                scheduleRepository.deleteById(scheduleId);
                break;

            case "AFTER":
                // 해당 날짜 이후(포함) 모든 일정 삭제
                List<Schedule> schedulesToDelete = scheduleRepository.findByRepeatGroupIdAndStartTimeGreaterThanEqual(
                        existingSchedule.getRepeatGroupId(), existingSchedule.getStartTime());

                scheduleEventProducer.sendDeletedSchedules(schedulesToDelete);

                // 외래 키 오류 방지를 위해 먼저 ShiftRequest 삭제
                List<Long> scheduleIdsToDelete = schedulesToDelete.stream().map(Schedule::getId).toList();
                shiftRequestRepository.deleteByScheduleIdIn(scheduleIdsToDelete);

                scheduleRepository.deleteAll(schedulesToDelete);
                break;

            case "ALL":
                // 전체 반복 일정 삭제
                List<Schedule> allSchedules = scheduleRepository.findByRepeatGroupId(existingSchedule.getRepeatGroupId());

                scheduleEventProducer.sendDeletedSchedules(allSchedules);
                // 외래 키 오류 방지를 위해 먼저 ShiftRequest 삭제
                List<Long> allScheduleIds = allSchedules.stream().map(Schedule::getId).toList();
                shiftRequestRepository.deleteByScheduleIdIn(allScheduleIds);

                scheduleRepository.deleteAll(allSchedules);
                break;

            default:
                throw new IllegalArgumentException("Invalid delete option: " + deleteOption);
        }
    }

    @Override
    public List<Schedule> createSchedulesForWorkers(OwnerScheduleRequest request) {
        List<Schedule> schedules = new ArrayList<>();
        List<Schedule> savedSchedules = new ArrayList<>();
        
        LocalDateTime startDateTime = LocalDateTime.of(request.getDate(), request.getStartTime());
        LocalDateTime endDateTime = LocalDateTime.of(request.getDate(), request.getEndTime());
        
        Date startTime = convertToDate(startDateTime);
        Date endTime = convertToDate(endDateTime);
        
        // 각 워커 ID에 대해 스케줄 생성
        for (Long workerId : request.getWorkerIds()) {
            // 중복 체크
            if (scheduleRepository.existsOverlappingSchedule(
                    workerId, 
                    startTime, 
                    endTime, 
                    null)) {
                throw new ScheduleConflictException("해당 시간에 이미 다른 스케줄이 존재합니다. 사용자 ID: " + workerId);
            }
            
            String workerName = userService.getUserNameById(workerId);
            
            Schedule schedule = Schedule.builder()
                    .branchId(request.getBranchId())
                    .workerId(workerId)
                    .workerName(workerName)
                    .workType(request.getWorkType())
                    .inputType(request.getInputType() != null ? request.getInputType() : 1) // 기본값 1 (기본근무)
                    .startTime(startTime)
                    .endTime(endTime)
                    .lastUpdated(new Date())
                    .build();
            
            schedules.add(schedule);
        }
        
        // 모든 스케줄 저장
        savedSchedules = scheduleRepository.saveAll(schedules);
        
        // 이벤트 발행
        scheduleEventProducer.sendSchedules(savedSchedules);
        
        return savedSchedules;
    }

    @Override
    public List<Schedule> createRepeatSchedulesForWorkers(OwnerRepeatScheduleRequest request) {
        List<Schedule> allSavedSchedules = new ArrayList<>();
        
        // 모든 근무자에 대해 반복 일정 생성
        for (Long workerId : request.getWorkerIds()) {
            String workerName = userService.getUserNameById(workerId);
            
            // 해당 근무자에 대한 RepeatScheduleRequest 생성
            RepeatScheduleRequest repeatRequest = new RepeatScheduleRequest();
            repeatRequest.setBranchId(request.getBranchId());
            repeatRequest.setWorkerId(workerId);
            repeatRequest.setWorkerName(workerName);
            repeatRequest.setStartTime(request.getStartTime());
            repeatRequest.setEndTime(request.getEndTime());
            repeatRequest.setWorkType(request.getWorkType());
            repeatRequest.setRepeat(convertRepeat(request.getRepeat()));
            
            // 근무자 일정 생성 (기존 createRepeatSchedules 메소드 사용)
            try {
                List<Schedule> workerSchedules = createRepeatSchedules(repeatRequest);
                allSavedSchedules.addAll(workerSchedules);
            } catch (ScheduleConflictException e) {
                // 한 근무자가 실패했을 때 예외 정보에 근무자 ID 추가
                throw new ScheduleConflictException("근무자 ID " + workerId + "의 스케줄 생성 실패: " + e.getMessage());
            }
        }
        
        return allSavedSchedules;
    }

    // OwnerRepeatScheduleRequest.Repeat를 RepeatScheduleRequest.Repeat로 변환
    private RepeatScheduleRequest.Repeat convertRepeat(OwnerRepeatScheduleRequest.Repeat ownerRepeat) {
        RepeatScheduleRequest.Repeat repeat = new RepeatScheduleRequest.Repeat();
        repeat.setType(ownerRepeat.getType());
        repeat.setDaysOfWeek(ownerRepeat.getDaysOfWeek());
        repeat.setStartDate(ownerRepeat.getStartDate());
        repeat.setEndDate(ownerRepeat.getEndDate());
        return repeat;
    }

    @Override
    public List<Schedule> getSchedulesByBranchIdAndDateRange(Long branchId, LocalDate startDate, LocalDate endDate) {
        // 시작 날짜는 00:00:00, 종료 날짜는 23:59:59로 설정
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        Date startTime = convertToDate(startDateTime);
        Date endTime = convertToDate(endDateTime);
        
        return scheduleRepository.findByBranchIdAndDateRange(branchId, startTime, endTime);
    }

    @Override
    public List<Schedule> getSchedulesByBranchIdAndUserIdAndDateRange(Long branchId, Long userId, LocalDate startDate, LocalDate endDate) {
        // 시작 날짜는 00:00:00, 종료 날짜는 23:59:59로 설정
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        Date startTime = convertToDate(startDateTime);
        Date endTime = convertToDate(endDateTime);
        
        return scheduleRepository.findByBranchIdAndUserIdAndDateRange(branchId, userId, startTime, endTime);
    }
}
