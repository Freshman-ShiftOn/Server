package com.example.calendarservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Time;
import java.util.Date;

@Builder
@Entity
@Table(name = "req_shift")
@EntityListeners(AuditingEntityListener.class) // Auditing 활성화
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShiftRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "req_start_time", nullable = false)
    private Time reqStartTime;

    @Column(name = "req_end_time", nullable = false)
    private Time reqEndTime;

    @Column(name = "work_type", length = 10, nullable = false)
    private String workType;

    @Column(name = "worker_id", nullable = false)
    private String workerId;

    @Column(name = "branch_id", nullable = false)
    private Integer branchId;

    @Column(name = "accept_id")
    private String acceptId;

    @Column(name = "req_status", length = 10, nullable = false)
    private String reqStatus;

    @LastModifiedDate // 자동 업데이트
    @Column(name = "last_updated", nullable = false)
    private Date lastUpdated;
}