package com.example.calendarservice.service;

import com.example.calendarservice.exception.ResourceNotFoundException;
import com.example.calendarservice.model.Schedule;
import com.example.calendarservice.model.ShiftRequest;
import com.example.calendarservice.repository.ScheduleRepository;
import com.example.calendarservice.repository.ShiftRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShiftRequestServiceImpl implements ShiftRequestService {

    private final ShiftRequestRepository shiftRequestRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public ShiftRequest createShiftRequest(ShiftRequest shiftRequest) {
        // Validate that the schedule exists
        if (!scheduleRepository.existsById(shiftRequest.getSchedule().getId())) {
            throw new ResourceNotFoundException("Schedule not found with id " + shiftRequest.getSchedule().getId());
        }
        return shiftRequestRepository.save(shiftRequest);
    }

    @Override
    public ShiftRequest updateShiftRequest(Integer shiftRequestId, ShiftRequest shiftRequest) {
        ShiftRequest existingShiftRequest = shiftRequestRepository.findById(shiftRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftRequest not found with id " + shiftRequestId));

        existingShiftRequest.setReqStartTime(shiftRequest.getReqStartTime());
        existingShiftRequest.setReqEndTime(shiftRequest.getReqEndTime());
        existingShiftRequest.setWorkType(shiftRequest.getWorkType());
        existingShiftRequest.setWorkerId(shiftRequest.getWorkerId());
        existingShiftRequest.setBranchId(shiftRequest.getBranchId());
        existingShiftRequest.setReqStatus(shiftRequest.getReqStatus());

        return shiftRequestRepository.save(existingShiftRequest);
    }

    @Override
    public void deleteShiftRequest(Integer shiftRequestId) {
        if (!shiftRequestRepository.existsById(shiftRequestId)) {
            throw new ResourceNotFoundException("ShiftRequest not found with id " + shiftRequestId);
        }
        shiftRequestRepository.deleteById(shiftRequestId);
    }

    @Override
    public boolean isUserShiftRequest(Integer reqShiftId, String workerId) {
        return shiftRequestRepository.existsByIdAndWorkerId(reqShiftId, workerId);
    }

    @Override
    public ShiftRequest acceptShiftRequest(Integer shiftRequestId, String acceptId) {
        // 대타 요청 확인
        ShiftRequest existingShiftRequest = shiftRequestRepository.findById(shiftRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftRequest not found with id " + shiftRequestId));

        // 스케줄 가져오기
        Schedule schedule = existingShiftRequest.getSchedule();
        if (schedule == null) {
            throw new ResourceNotFoundException("Schedule not associated with ShiftRequest id " + shiftRequestId);
        }

        // 대타 요청 수락 처리
        existingShiftRequest.setAcceptId(acceptId);
        existingShiftRequest.setReqStatus("ACCEPTED");
        shiftRequestRepository.save(existingShiftRequest);

        // 스케줄 업데이트 (대타 요청 정보를 스케줄에 반영)
        schedule.setWorkerId(Integer.parseInt(acceptId)); // 대타를 수락한 유저로 스케줄 변경
        schedule.setWorkType(existingShiftRequest.getWorkType());
        schedule.setStartTime(existingShiftRequest.getReqStartTime());
        schedule.setEndTime(existingShiftRequest.getReqEndTime());
        scheduleRepository.save(schedule);

        return existingShiftRequest;
    }
}
