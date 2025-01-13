package com.example.calendarservice.repository;

import com.example.calendarservice.model.ShiftRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Integer> {
    boolean existsByIdAndWorkerId(Integer id, String workerId);
    List<ShiftRequest> findByWorkerId(String workerId);
    List<ShiftRequest> findByAcceptId(String workerId);
}
