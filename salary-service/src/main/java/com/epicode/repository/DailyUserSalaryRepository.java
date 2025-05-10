package com.epicode.repository;


import com.epicode.domain.DailyUserSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyUserSalaryRepository extends JpaRepository<DailyUserSalary, Long> {

    Optional<DailyUserSalary> findByUserIdAndBranchIdAndWorkDate(Long userId, Long branchId, LocalDate workDate);
}
