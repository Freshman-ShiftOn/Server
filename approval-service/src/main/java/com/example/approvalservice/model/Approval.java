package com.example.approvalservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Entity
@Table(name = "approval")
@EntityListeners(AuditingEntityListener.class) // Auditing 활성화
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "worker_id", nullable = false)
    private Long userId;       // 요청한 사용자 ID

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;   // 관련된 스케줄 ID

    @Column(name = "status", nullable = false)
    private String status;     // PENDING, APPROVED, REJECTED

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

}
