package com.epicode.repository;

import com.epicode.domain.SpecificTimeSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpecificTimeSalaryRepository extends JpaRepository<SpecificTimeSalary, Long> {
    // 특정 사용자와 지점에 연결된 특별 시급 조회
    @Query("SELECT sts FROM SpecificTimeSalary sts JOIN sts.salary s WHERE s.userId = :userId AND s.branchId = :branchId")
    List<SpecificTimeSalary> findByUserIdAndBranchId(@Param("userId") Long userId, @Param("branchId") Long branchId);
}