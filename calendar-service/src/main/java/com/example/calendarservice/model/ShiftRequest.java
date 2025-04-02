package com.example.calendarservice.model;

import com.example.calendarservice.util.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Time;
import java.util.Date;
import java.util.List;

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
    private Long id;

    @Column(name = "worker_id", nullable = false)
    private Long workerId;

    @Column(name = "worker_name", nullable = false)
    private String workerName;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Convert(converter = StringListConverter.class) // 리스트를 JSON으로 변환
    @Column(name = "work_type", columnDefinition = "TEXT")
    private List<String> workType;

    @Column(name = "req_start_time", nullable = false)
    private Time reqStartTime;

    @Column(name = "req_end_time", nullable = false)
    private Time reqEndTime;

    @Column(name = "accept_id")
    private Long acceptId;

    @Column(name = "req_status", length = 10, nullable = false)
    private String reqStatus;

    @Column(name = "reason")
    private String reason;

    @LastModifiedDate // 자동 업데이트
    @Column(name = "last_updated", nullable = false)
    private Date lastUpdated;
}