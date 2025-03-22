package com.example.approvalservice.service;

import com.example.approvalservice.model.ShiftRequest;

import java.util.List;

public interface ShiftRequestService {
    ShiftRequest createShiftRequest(ShiftRequest shiftRequest);
    ShiftRequest updateShiftRequest(Long shiftRequestId, String reason);
    void deleteShiftRequest(Long shiftRequestId);

    boolean isUserShiftRequest(Long reqShiftId, Long workerId);

    ShiftRequest acceptShiftRequest(Long shiftRequestId, Long acceptId);

    List<ShiftRequest> getShiftRequestsByUser(Long workerId);
    List<ShiftRequest> getAcceptedShiftRequestsByUser(Long workerId);

    List<ShiftRequest> getShiftRequestsByBranchAndMonth(Long branchId, Integer month);
}