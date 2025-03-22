package com.example.approvalservice.model;

import com.example.approvalservice.util.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;

@Builder
@Entity
@Table(name = "schedule")
@EntityListeners(AuditingEntityListener.class) // Auditing 활성화
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "worker_id", nullable = false)
    private Long workerId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Convert(converter = StringListConverter.class) // 리스트를 JSON으로 변환
    @Column(name = "work_type", columnDefinition = "TEXT")
    private List<String> workType;

    @Column(name = "input_type", nullable = false)
    private Integer inputType;

     @Column(name = "repeat_group_id")
    private Long repeatGroupId;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    private Date endTime;

    @LastModifiedDate // 자동 업데이트
    @Column(name = "last_updated", nullable = false)
    private Date lastUpdated;
}
