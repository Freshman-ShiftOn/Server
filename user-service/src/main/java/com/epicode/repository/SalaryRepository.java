package com.epicode.repository;
import com.epicode.domain.Salary;
import com.epicode.domain.SalaryId;
import com.epicode.domain.SpecificTimeSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, SalaryId> {
    // userId + BranchId로 Salary만 조회
    @Query("SELECT s FROM Salary s WHERE s.userId = :userId AND s.branchId = :branchId")
    Optional<Salary> findByUserIdAndBranchId(@Param("userId") Long userId, @Param("branchId") Long branchId);

    // 특정 Branch의 모든 Salary 조회
    List<Salary> findAllByBranchId(Long branchId);

    // userId + BranchId로 특별 시급만 조회
    @Query("SELECT s FROM Salary s JOIN s.specificTimeSalaries sts WHERE s.branchId = :branchId AND s.userId = :userId")
    List<Salary> findSpecificTimeSalariesByBranchIdAndUserId(@Param("branchId") Long branchId, @Param("userId") Long userId);
}