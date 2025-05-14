package com.example.calendarservice.repository;

import com.example.calendarservice.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.branchId = :branchId AND FUNCTION('MONTH', s.startTime) = :month")
    List<Schedule> findByBranchIdAndMonth(@Param("branchId") Long branchId, @Param("month") Integer month);

    @Query("SELECT s FROM Schedule s WHERE s.branchId = :branchId AND s.workerId = :userId AND FUNCTION('MONTH', s.startTime) = :month")
    List<Schedule> findByBranchIdAndMonthAndUserId(@Param("branchId") Long branchId, @Param("month") Integer month, @Param("userId") Long userId);

    List<Schedule> findByRepeatGroupId(Long repeatGroupId);
    List<Schedule> findByRepeatGroupIdAndStartTimeGreaterThanEqual(Long repeatGroupId, Date startTime);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM schedule s " +
           "WHERE s.workerId = :workerId " +
           "AND (:excludeId IS NULL OR s.id != :excludeId) " +
           "AND NOT (s.endTime AT TIME ZONE 'Asia/Seoul' <= :startTime AT TIME ZONE 'Asia/Seoul' " +
           "OR s.startTime AT TIME ZONE 'Asia/Seoul' >= :endTime AT TIME ZONE 'Asia/Seoul'))", 
           nativeQuery = true)
    boolean existsOverlappingSchedule(
            @Param("workerId") Long workerId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("excludeId") Long excludeId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM schedule s " +
           "WHERE s.workerId = :workerId " +
           "AND NOT (s.endTime AT TIME ZONE 'Asia/Seoul' <= :startTime AT TIME ZONE 'Asia/Seoul' " +
           "OR s.startTime AT TIME ZONE 'Asia/Seoul' >= :endTime AT TIME ZONE 'Asia/Seoul'))", 
           nativeQuery = true)
    boolean existsOverlappingSchedule(
            @Param("workerId") Long workerId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);
}
