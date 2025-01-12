package com.example.calendarservice.service;

import com.example.calendarservice.model.ShiftRequest;

public interface ShiftRequestService {
    ShiftRequest createShiftRequest(ShiftRequest shiftRequest);
    ShiftRequest updateShiftRequest(Integer shiftRequestId, ShiftRequest shiftRequest);
    void deleteShiftRequest(Integer shiftRequestId);
    ShiftRequest acceptShiftRequest(Integer shiftRequestId, String acceptId);
}