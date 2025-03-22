package com.example.calendarservice.repository;

import com.example.calendarservice.model.ShiftRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Long> {
    boolean existsByIdAndWorkerId(Long id, Long workerId);
    boolean existsByScheduleId(Long scheduleId);
    List<ShiftRequest> findByWorkerId(Long workerId);
    List<ShiftRequest> findByAcceptId(Long workerId);

    @Modifying
    @Transactional
    void deleteByScheduleId(Long scheduleId);

    @Modifying
    @Transactional
    void deleteByScheduleIdIn(List<Long> scheduleIds);

    @Query("SELECT sr FROM ShiftRequest sr " +
            "JOIN Schedule s ON sr.scheduleId = s.id " +
            "WHERE s.branchId = :branchId " +
            "  AND FUNCTION('MONTH', s.startTime) = :month")
    List<ShiftRequest> findByBranchIdAndMonth(@Param("branchId") Long branchId,
                                              @Param("month") Integer month);
}
