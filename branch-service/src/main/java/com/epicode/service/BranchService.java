package com.epicode.service;
import com.epicode.dto.WorkerProjection;
import com.epicode.model.Branch;
import com.epicode.model.UserBranch;

import java.util.List;

public interface BranchService {
    List<String> getBranchNamesByUserId(Long userId);
    List<String> getBranchNamesByUserIds(Long[] branchIds);
    Long getBranchIdByName(String name);
    void createBranch(Branch branch, Long userId, String email);
    List<WorkerProjection> getWorkersByBranchId(Long branchId);
}