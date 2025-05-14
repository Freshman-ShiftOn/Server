package com.example.calendarservice.repository;

import com.example.calendarservice.model.ShiftRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Long> {
    boolean existsByIdAndWorkerId(Long id, Long workerId);
    boolean existsByScheduleId(Long scheduleId);
    Optional<ShiftRequest> findByScheduleId(Long scheduleId);
    List<ShiftRequest> findByWorkerId(Long workerId);
    List<ShiftRequest> findByAcceptId(Long acceptId);

    List<ShiftRequest> findByWorkerIdAndBranchId(Long workerId, Long branchId);
    List<ShiftRequest> findByAcceptIdAndBranchId(Long acceptId, Long branchId);

    @Query(value = "SELECT sr FROM ShiftRequest sr " +
           "WHERE (sr.workerId = :workerId) " +
           "AND (sr.branchId = :branchId) " +
           "AND CONVERT_TZ(sr.reqEndTime, '+00:00', 'Asia/Seoul') > CONVERT_TZ(CURRENT_TIMESTAMP, '+00:00', 'Asia/Seoul')", 
           nativeQuery = true)
    List<ShiftRequest> findActiveRequestsByWorkerIdAndBranchId(
            @Param("workerId") Long workerId,
            @Param("branchId") Long branchId);

    @Query(value = "SELECT sr FROM ShiftRequest sr " +
           "WHERE sr.workerId = :workerId " +
           "AND CONVERT_TZ(sr.reqEndTime, '+00:00', 'Asia/Seoul') > CONVERT_TZ(CURRENT_TIMESTAMP, '+00:00', 'Asia/Seoul')", 
           nativeQuery = true)
    List<ShiftRequest> findActiveRequestsByWorkerId(@Param("workerId") Long workerId);

    @Query(value = "SELECT sr FROM ShiftRequest sr " +
           "JOIN Schedule s ON sr.scheduleId = s.id " +
           "WHERE s.branchId = :branchId " +
           "AND MONTH(CONVERT_TZ(s.startTime, '+00:00', 'Asia/Seoul')) = :month " +
           "AND CONVERT_TZ(sr.reqEndTime, '+00:00', 'Asia/Seoul') > CONVERT_TZ(CURRENT_TIMESTAMP, '+00:00', 'Asia/Seoul')", 
           nativeQuery = true)
    List<ShiftRequest> findActiveRequestsByBranchIdAndMonth(
            @Param("branchId") Long branchId,
            @Param("month") Integer month);

    @Modifying
    @Transactional
    void deleteByScheduleId(Long scheduleId);

    @Modifying
    @Transactional
    void deleteByScheduleIdIn(List<Long> scheduleIds);

    @Query(value = "SELECT sr FROM ShiftRequest sr " +
            "JOIN Schedule s ON sr.scheduleId = s.id " +
            "WHERE s.branchId = :branchId " +
            "AND MONTH(CONVERT_TZ(s.startTime, '+00:00', 'Asia/Seoul')) = :month", 
            nativeQuery = true)
    List<ShiftRequest> findByBranchIdAndMonth(@Param("branchId") Long branchId,
                                              @Param("month") Integer month);
}
