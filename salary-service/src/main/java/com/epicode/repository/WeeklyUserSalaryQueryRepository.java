package com.epicode.repository;

import com.epicode.dto.WeeklySalaryDto;

import java.util.Optional;

public interface WeeklyUserSalaryQueryRepository {
    Optional<WeeklySalaryDto> findWeeklySalary(Long userId, Long branchId, int month, int week);
}