package com.example.calendarservice.service;

import com.example.calendarservice.exception.ResourceNotFoundException;
import com.example.calendarservice.model.Schedule;
import com.example.calendarservice.model.ShiftRequest;
import com.example.calendarservice.repository.ScheduleRepository;
import com.example.calendarservice.repository.ShiftRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftRequestServiceImpl implements ShiftRequestService {

    private final ShiftRequestRepository shiftRequestRepository;
    private final ScheduleRepository scheduleRepository;

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
    public ShiftRequest acceptShiftRequest(Long shiftRequestId, Long acceptId) {
        // 대타 요청 확인
        ShiftRequest existingShiftRequest = shiftRequestRepository.findById(shiftRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftRequest not found with id " + shiftRequestId));

        // 스케줄 가져오기
        Schedule schedule = getScheduleFromShiftRequest(shiftRequestId);
        if (schedule == null) {
            throw new ResourceNotFoundException("Schedule not associated with ShiftRequest id " + shiftRequestId);
        }

        // 대타 요청 수락 처리
        existingShiftRequest.setAcceptId(acceptId);
        existingShiftRequest.setReqStatus("ACCEPTED");
        shiftRequestRepository.save(existingShiftRequest);

        Date reqStartTime = existingShiftRequest.getReqStartTime();
        Date reqEndTime = existingShiftRequest.getReqEndTime();
        Date originalStartTime = schedule.getStartTime();
        Date originalEndTime = schedule.getEndTime();

        // 기존 스케줄을 분리
        List<Schedule> newSchedules = new ArrayList<>();

        // 1. 기존 스케줄의 시작 시간이 대타 요청보다 빠른 경우
        if (originalStartTime.before(reqStartTime)) {
            newSchedules.add(Schedule.builder()
                    .branchId(schedule.getBranchId())
                    .workerId(schedule.getWorkerId()) // 요청자가 남는 부분
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
                    .workType(schedule.getWorkType())
                    .startTime(reqEndTime)
                    .endTime(originalEndTime)
                    .lastUpdated(new Date())
                    .build());
        }

        // 3. 대타 요청 시간에 해당하는 스케줄 생성 (수락자)
        schedule.setWorkerId(acceptId); // 대타를 수락한 유저로 변경
        schedule.setStartTime(reqStartTime);
        schedule.setEndTime(reqEndTime);
        schedule.setWorkType(existingShiftRequest.getWorkType());

        // 기존 스케줄 저장
        scheduleRepository.save(schedule);

        // 분리된 스케줄 저장
        for (Schedule newSchedule : newSchedules) {
            scheduleRepository.save(newSchedule);
        }

        return existingShiftRequest;
    }

    @Override
    public List<ShiftRequest> getShiftRequestsByUser(Long workerID){
        return shiftRequestRepository.findByWorkerId(workerID);
    }

    @Override
    public List<ShiftRequest> getAcceptedShiftRequestsByUser(Long userId) {
        return shiftRequestRepository.findByAcceptId(userId);
    }

    private Schedule getScheduleFromShiftRequest(Long shiftRequestId) {
        ShiftRequest shiftRequest = shiftRequestRepository.findById(shiftRequestId)
                .orElseThrow(() -> new RuntimeException("ShiftRequest not found"));

        return scheduleRepository.findById(shiftRequest.getScheduleId())
                .orElse(null); // ✅ scheduleId를 기반으로 Schedule 조회
    }
}
