package com.example.calendarservice.service;

import com.example.calendarservice.model.ShiftRequest;

import java.util.List;

public interface ShiftRequestService {
    ShiftRequest createShiftRequest(ShiftRequest shiftRequest);
    ShiftRequest updateShiftRequest(Integer shiftRequestId, ShiftRequest shiftRequest);
    void deleteShiftRequest(Integer shiftRequestId);

    boolean isUserShiftRequest(Integer reqShiftId, String workerId);

    ShiftRequest acceptShiftRequest(Integer shiftRequestId, String acceptId);

    List<ShiftRequest> getShiftRequestsByUser(String workerId);
    List<ShiftRequest> getAcceptedShiftRequestsByUser(String workerId);
}