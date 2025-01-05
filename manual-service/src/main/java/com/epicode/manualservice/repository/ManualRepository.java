package com.epicode.manualservice.repository;

import com.epicode.manualservice.model.Manual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManualRepository extends JpaRepository<Manual, Integer> {
    List<Manual> findByBranchId(Integer branchId);
}
