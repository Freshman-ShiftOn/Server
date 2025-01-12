package com.example.calendarservice.repository;

import com.example.calendarservice.model.ShiftRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Integer> {
    boolean existsByIdAndWorkerId(Integer id, String workerId);
}
