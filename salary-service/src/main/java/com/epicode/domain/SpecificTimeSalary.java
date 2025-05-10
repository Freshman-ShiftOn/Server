package com.epicode.domain;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Time;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "specific_time_salary")
public class SpecificTimeSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성 전략
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id")
    })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Salary salary;

    @Column(name = "specific_days", nullable = false)
    private String specificDays; // 특정 요일 (쉼표로 구분된 요일 문자열)

    @Column(name = "start_time", nullable = false)
    private Time startTime; // 특정 시간대 시작 시간

    @Column(name = "end_time", nullable = false)
    private Time endTime; // 특정 시간대 종료 시간

    @Column(name = "specific_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal specificSalary; // 특정 시간대의 시급
}
