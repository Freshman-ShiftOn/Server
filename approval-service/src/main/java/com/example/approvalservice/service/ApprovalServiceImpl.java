package com.example.approvalservice.service;

import com.example.approvalservice.model.Approval;
import com.example.approvalservice.repository.ApprovalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {
    private final ApprovalRepository approvalRepository;

//    public ApprovalService(ApprovalRepository approvalRepository) {
//        this.approvalRepository = approvalRepository;
//        //this.kafkaTemplate = kafkaTemplate;
//    }

    public Approval requestApproval(Long userId, Long scheduleId) {
        Approval approval = new Approval();
        approval.setUserId(userId);
        approval.setScheduleId(scheduleId);
        approval.setStatus("PENDING");
        return approvalRepository.save(approval);
    }

    public void approve(Long approvalId) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        approval.setStatus("APPROVED");
        approvalRepository.save(approval);

        // Kafka 이벤트 발행
        // kafkaTemplate.send("approval-topic", "APPROVED:" + approval.getId());
    }

    public void reject(Long approvalId) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        approval.setStatus("REJECTED");
        approvalRepository.save(approval);

        // Kafka 이벤트 발행
        // kafkaTemplate.send("approval-topic", "REJECTED:" + approval.getId());
    }
}
