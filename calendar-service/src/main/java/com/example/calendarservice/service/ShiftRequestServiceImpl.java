package com.example.calendarservice.service;

import com.example.calendarservice.dto.ShiftRequestDto;
import com.example.calendarservice.exception.ResourceNotFoundException;
import com.example.calendarservice.exception.ScheduleConflictException;
import com.example.calendarservice.model.Branch;
import com.example.calendarservice.model.Schedule;
import com.example.calendarservice.model.ShiftRequest;
import com.example.calendarservice.repository.BranchRepository;
import com.example.calendarservice.repository.ScheduleRepository;
import com.example.calendarservice.repository.ShiftRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftRequestServiceImpl implements ShiftRequestService {

    private final ShiftRequestRepository shiftRequestRepository;
    private final ScheduleRepository scheduleRepository;
    private final BranchRepository branchRepository;

    @Override
    public ShiftRequest createShiftRequest(ShiftRequest shiftRequest) {
        // Validate that the schedule exists
        if (!scheduleRepository.existsById(shiftRequest.getScheduleId())) {
            throw new ResourceNotFoundException("Schedule not found with id : " + shiftRequest.getScheduleId());
        }
        if (shiftRequestRepository.existsByScheduleId(shiftRequest.getScheduleId())) {
            throw new ResourceNotFoundException("ScheduleRequest already with schedule id : " + shiftRequest.getScheduleId());
        }
        return shiftRequestRepository.save(shiftRequest);
    }

    @Override
    public ShiftRequest updateShiftRequest(Long shiftRequestId, String reason) {
        ShiftRequest existingShiftRequest = shiftRequestRepository.findById(shiftRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftRequest not found with id " + shiftRequestId));

        existingShiftRequest.setReason(reason);

        return shiftRequestRepository.save(existingShiftRequest);
    }

    @Override
    public void deleteShiftRequest(Long shiftRequestId) {
        if (!shiftRequestRepository.existsById(shiftRequestId)) {
            throw new ResourceNotFoundException("ShiftRequest not found with id " + shiftRequestId);
        }
        shiftRequestRepository.deleteById(shiftRequestId);
    }

    @Override
    public boolean isUserShiftRequest(Long reqShiftId, Long workerId) {
        return shiftRequestRepository.existsByIdAndWorkerId(reqShiftId, workerId);
    }

    @Override
    public ShiftRequest acceptShiftRequest(Long shiftRequestId, Long acceptId, String acceptName) {
        // 대타 요청 확인
        ShiftRequest shiftRequest = shiftRequestRepository.findById(shiftRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftRequest not found with id " + shiftRequestId));

        // 이미 수락된 대타 요청인지 확인
        if (shiftRequest.getAcceptId() != null) {
            throw new IllegalStateException("This shift request has already been accepted.");
        }

        // 대타 요청에 해당하는 스케줄 조회
        Schedule schedule = scheduleRepository.findById(shiftRequest.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id " + shiftRequest.getScheduleId()));

        // 수락하려는 사람의 스케줄 중복 검사
        if (scheduleRepository.existsOverlappingSchedule(
                acceptId,
                schedule.getStartTime(),
                schedule.getEndTime())) {
            throw new ScheduleConflictException("해당 시간에 이미 다른 스케줄이 존재하여 대타 요청을 수락할 수 없습니다.");
        }

        // 대타 요청 수락 처리
        shiftRequest.setAcceptId(acceptId);
        shiftRequest.setAcceptName(acceptName);
        shiftRequest.setReqStatus("ACCEPTED");
        shiftRequestRepository.save(shiftRequest);

        Date reqStartTime = shiftRequest.getReqStartTime();
        Date reqEndTime = shiftRequest.getReqEndTime();
        Date originalStartTime = schedule.getStartTime();
        Date originalEndTime = schedule.getEndTime();

        // 기존 스케줄을 분리
        List<Schedule> newSchedules = new ArrayList<>();

        // 1. 기존 스케줄의 시작 시간이 대타 요청보다 빠른 경우
        if (originalStartTime.before(reqStartTime)) {
            newSchedules.add(Schedule.builder()
                    .branchId(schedule.getBranchId())
                    .workerId(schedule.getWorkerId()) // 요청자가 남는 부분
                    .workerName(schedule.getWorkerName()) // 요청자가 남는 부분
                    .workType(schedule.getWorkType())
                    .startTime(originalStartTime)
                    .endTime(reqStartTime)
                    .lastUpdated(new Date())
                    .build());
        }

        // 2. 기존 스케줄의 끝 시간이 대타 요청보다 늦은 경우
        if (originalEndTime.after(reqEndTime)) {
            newSchedules.add(Schedule.builder()
                    .branchId(schedule.getBranchId())
                    .workerId(schedule.getWorkerId()) // 요청자가 남는 부분
                    .workerName(schedule.getWorkerName()) // 요청자가 남는 부분
                    .workType(schedule.getWorkType())
                    .startTime(reqEndTime)
                    .endTime(originalEndTime)
                    .lastUpdated(new Date())
                    .build());
        }

        // 3. 대타 요청 시간에 해당하는 스케줄 생성 (수락자)
        schedule.setWorkerId(acceptId); // 대타를 수락한 유저로 변경
        schedule.setWorkerName(acceptName); // 대타를 수락한 유저로 변경
        schedule.setStartTime(reqStartTime);
        schedule.setEndTime(reqEndTime);
        schedule.setWorkType(shiftRequest.getWorkType());

        // 기존 스케줄 저장
        scheduleRepository.save(schedule);

        // 분리된 스케줄 저장
        for (Schedule newSchedule : newSchedules) {
            scheduleRepository.save(newSchedule);
        }

        return shiftRequest;
    }

    @Override
    public List<ShiftRequestDto> getShiftRequestsByUser(Long workerId, Long branchId){
        List<ShiftRequest> shiftRequests;

        if (branchId != null) {
            shiftRequests = shiftRequestRepository.findByWorkerIdAndBranchId(workerId, branchId);
        } else {
            shiftRequests = shiftRequestRepository.findByWorkerId(workerId);
        }

        return shiftRequests.stream()
                .map(shift -> {
                    ShiftRequestDto dto = new ShiftRequestDto();
                    BeanUtils.copyProperties(shift, dto);
                    String branchName = getBranchNameById(shift.getBranchId());
                    dto.setBranchName(branchName);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftRequestDto> getAcceptedShiftRequestsByUser(Long acceptId, Long branchId) {
        List<ShiftRequest> acceptedRequests;

        if (branchId != null) {
            acceptedRequests = shiftRequestRepository.findByAcceptIdAndBranchId(acceptId, branchId);
        } else {
            acceptedRequests = shiftRequestRepository.findByAcceptId(acceptId);
        }

        return acceptedRequests.stream()
                .map(shift -> {
                    ShiftRequestDto dto = new ShiftRequestDto();
                    BeanUtils.copyProperties(shift, dto);
                    String branchName = getBranchNameById(shift.getBranchId());
                    dto.setBranchName(branchName);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String getBranchNameById(Long id) {
        return branchRepository.findById(id)
                .map(Branch::getName)
                .orElse("미등록 지점");
    }

    private Schedule getScheduleFromShiftRequest(Long shiftRequestId) {
        ShiftRequest shiftRequest = shiftRequestRepository.findById(shiftRequestId)
                .orElseThrow(() -> new RuntimeException("ShiftRequest not found"));

        return scheduleRepository.findById(shiftRequest.getScheduleId())
                .orElse(null); // ✅ scheduleId를 기반으로 Schedule 조회
    }

    public List<ShiftRequest> getShiftRequestsByBranchAndMonth(Long branchId, Integer month) {
        // Repository를 통해 조건에 맞는 ShiftRequest 리스트를 조회.
        // 예) 아래처럼 쿼리 메서드를 작성하거나 Query DSL, JPQL 등을 활용할 수 있습니다.
        return shiftRequestRepository.findByBranchIdAndMonth(branchId, month);
    }
}
