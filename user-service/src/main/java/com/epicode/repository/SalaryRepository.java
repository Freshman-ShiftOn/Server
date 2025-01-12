package com.epicode.repository;
import com.epicode.domain.Salary;
import com.epicode.domain.SalaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, SalaryId> {
    // 사용자와 Branch로 Salary 조회
    @Query("SELECT s FROM Salary s WHERE s.userId = :userId AND s.branchId = :branchId")
    Optional<Salary> findByUserIdAndBranchId(@Param("userId") Long userId, @Param("branchId") Long branchId);
    List<Salary> findAllByBranchId(Long branchId); // 특정 Branch의 모든 Salary 조회
}