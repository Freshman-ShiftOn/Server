package com.example.approvalservice.controller;

import com.example.approvalservice.model.Approval;
import com.example.approvalservice.service.ApprovalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
@Tag(name = "approval-service-controller", description = "Approval 서비스 API")
public class ApprovalController {
    private final ApprovalService approvalService;

    @PostMapping("/request")
    public ResponseEntity<Approval> requestApproval(@RequestParam Long userId, @RequestParam Long scheduleId) {
        return ResponseEntity.ok(approvalService.requestApproval(userId, scheduleId));
    }

    @PostMapping("/{approvalId}/approve")
    public ResponseEntity<String> approve(@PathVariable Long approvalId) {
        approvalService.approve(approvalId);
        return ResponseEntity.ok("Approval Approved");
    }

    @PostMapping("/{approvalId}/reject")
    public ResponseEntity<String> reject(@PathVariable Long approvalId) {
        approvalService.reject(approvalId);
        return ResponseEntity.ok("Approval Rejected");
    }
}
