package com.example.calendarservice.service;

import com.example.calendarservice.model.ShiftRequest;

import java.util.List;

public interface ShiftRequestService {
    ShiftRequest createShiftRequest(ShiftRequest shiftRequest);
    ShiftRequest updateShiftRequest(Long shiftRequestId, ShiftRequest shiftRequest);
    void deleteShiftRequest(Long shiftRequestId);

    boolean isUserShiftRequest(Long reqShiftId, Long workerId);

    ShiftRequest acceptShiftRequest(Long shiftRequestId, Long acceptId);

    List<ShiftRequest> getShiftRequestsByUser(Long workerId);
    List<ShiftRequest> getAcceptedShiftRequestsByUser(Long workerId);
}