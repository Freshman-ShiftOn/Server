package com.epicode.domain;

import com.epicode.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "daily_user_salary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "branch_id", "work_date"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyUserSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Convert(converter = StringListConverter.class) // 리스트를 JSON으로 변환
    @Column(name = "work_type", columnDefinition = "TEXT")
    private List<String> workType;

    @Column(name = "worked_minutes", nullable = false)
    private Integer workedMinutes;

    @Column(name = "daily_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailySalary;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
