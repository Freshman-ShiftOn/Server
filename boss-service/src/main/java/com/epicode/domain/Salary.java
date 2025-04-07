package com.epicode.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(SalaryId.class)
@Table(
        name = "salary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "branch_id"}) // 복합 키 유니크 설정
)
public class Salary {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "basic_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal basicSalary; //기본 시급

    @Column(name = "weekly_allowance", nullable = false)
    private Boolean weeklyAllowance; //주휴수당 여부

    @Column(name = "payment_date")
    private Integer paymentDate; //급여 지급일

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "salary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecificTimeSalary> specificTimeSalaries; // 특정 시간대 시급 리스트
}