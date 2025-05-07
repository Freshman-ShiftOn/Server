package com.example.calendarservice.service;

import com.example.calendarservice.dto.ShiftRequestDto;
import com.example.calendarservice.model.ShiftRequest;

import java.util.List;

public interface ShiftRequestService {
    ShiftRequest createShiftRequest(ShiftRequest shiftRequest);
    ShiftRequest updateShiftRequest(Long shiftRequestId, String reason);
    void deleteShiftRequest(Long shiftRequestId);

    boolean isUserShiftRequest(Long reqShiftId, Long workerId);

    ShiftRequest acceptShiftRequest(Long shiftRequestId, Long acceptId, String acceptName);

    List<ShiftRequestDto> getShiftRequestsByUser(Long workerId, Long branchId);
    List<ShiftRequestDto> getAcceptedShiftRequestsByUser(Long workerId, Long branchId);

    List<ShiftRequest> getShiftRequestsByBranchAndMonth(Long branchId, Integer month);
}