package com.epicode.repository;

import com.epicode.domain.WeeklyUserSalary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeeklyUserSalaryRepository extends JpaRepository<WeeklyUserSalary, Long> {
    Optional<WeeklyUserSalary> findByUserIdAndBranchIdAndYearAndMonthAndWeek(
            Long userId, Long branchId, Integer year, Integer month, Integer week);
}