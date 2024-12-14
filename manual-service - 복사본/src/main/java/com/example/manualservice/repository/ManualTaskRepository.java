package com.example.manualservice.repository;

import com.example.manualservice.model.ManualTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManualTaskRepository extends JpaRepository<ManualTask, Integer> {
    List<ManualTask> findByManualId(Integer manualId);
}
