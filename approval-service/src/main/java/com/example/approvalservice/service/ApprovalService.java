package com.example.approvalservice.service;

import com.example.approvalservice.model.Approval;

public interface ApprovalService {
    Approval requestApproval(Long userId, Long scheduleId);
    void approve(Long approvalId);
    void reject(Long approvalId);

}
