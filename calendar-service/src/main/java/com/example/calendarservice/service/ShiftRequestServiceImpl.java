package com.example.calendarservice.service;

import com.example.calendarservice.exception.ResourceNotFoundException;
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
    public ShiftRequest acceptShiftRequest(Integer shiftRequestId, String acceptId) {
        ShiftRequest existingShiftRequest = shiftRequestRepository.findById(shiftRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("ShiftRequest not found with id " + shiftRequestId));

        existingShiftRequest.setAcceptId(acceptId);
        existingShiftRequest.setReqStatus("ACCEPTED");

        return shiftRequestRepository.save(existingShiftRequest);
    }
}
