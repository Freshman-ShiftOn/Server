package com.epicode.repository;


import com.epicode.domain.DailyUserSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyUserSalaryRepository extends JpaRepository<DailyUserSalary, Long> {
    Optional<DailyUserSalary> findByUserIdAndBranchIdAndWorkDate(Long userId, Long branchId, LocalDate workDate);
    @Query("""
    SELECT d FROM DailyUserSalary d
    WHERE d.branchId = :branchId
      AND d.workDate BETWEEN :start AND :end
      ORDER BY d.workDate ASC, d.workTime ASC
""")
    List<DailyUserSalary> findSalariesByBranchAndPeriod(
            @Param("branchId") Long branchId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

//    @Query("""
//    SELECT d FROM DailyUserSalary d
//    WHERE
//""")
//    List<DailyUserSalary> findSalariesByBranchAndPeriod(
//            @Param("branchId") Long branchId,
//            @Param("start") LocalDate start,
//            @Param("end") LocalDate end
//    );
}