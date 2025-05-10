package com.epicode.repository;

import com.epicode.dto.WeeklySalaryDto;
import java.util.*;

public interface WeeklyUserSalaryQueryRepository {
    List<WeeklySalaryDto> findWeeklySalary(Long branchId, int month);
    List<WeeklySalaryDto> findMonthlySalary(Long branchId, int month);
}