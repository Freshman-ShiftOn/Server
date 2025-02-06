package com.example.calendarservice.repository;

import com.example.calendarservice.model.ShiftRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Integer> {
    boolean existsByIdAndWorkerId(Integer id, Integer workerId);
    List<ShiftRequest> findByWorkerId(Integer workerId);
    List<ShiftRequest> findByAcceptId(Integer workerId);
}
