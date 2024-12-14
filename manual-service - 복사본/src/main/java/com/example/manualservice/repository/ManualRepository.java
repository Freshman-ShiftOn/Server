package com.example.manualservice.repository;

import com.example.manualservice.model.Manual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManualRepository extends JpaRepository<Manual, Integer> {
    List<Manual> findByBranchId(Integer branchId);
    Optional<Manual> findByIdAndBranchId(Integer id, Integer branchId);
}
