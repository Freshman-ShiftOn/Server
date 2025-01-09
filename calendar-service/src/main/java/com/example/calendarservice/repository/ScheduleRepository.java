package com.example.calendarservice.repository;

import com.example.calendarservice.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s FROM Schedule s WHERE s.branchId = :branchId AND FUNCTION('MONTH', s.startTime) = :month")
    List<Schedule> findByBranchIdAndMonth(@Param("branchId") Integer branchId, @Param("month") Integer month);

    @Query("SELECT s FROM Schedule s WHERE s.branchId = :branchId AND s.workerId = :userId AND FUNCTION('MONTH', s.startTime) = :month")
    List<Schedule> findByBranchIdAndMonthAndUserId(@Param("branchId") Integer branchId, @Param("month") Integer month, @Param("userId") Integer userId);
}
