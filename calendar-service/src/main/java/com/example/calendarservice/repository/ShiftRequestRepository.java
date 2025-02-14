package com.example.calendarservice.repository;

import com.example.calendarservice.model.ShiftRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Long> {
    boolean existsByIdAndWorkerId(Long id, Long workerId);
    boolean existsByScheduleId(Long scheduleId);
    List<ShiftRequest> findByWorkerId(Long workerId);
    List<ShiftRequest> findByAcceptId(Long workerId);
}
