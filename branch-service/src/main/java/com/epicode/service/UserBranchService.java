package com.epicode.service;

import com.epicode.dto.WorkerRequestDTO;

public interface UserBranchService {
    void addUserAtBranch(WorkerRequestDTO dto);
    void joinBranch(Long userId, Long branchId);
    String deleteUserBranchByEmailAndBranchId(String email, Long branchId);
    void updateUserAtBranch(WorkerRequestDTO dto);
    void deleteUserFromBranch(Long userId, Long branchId);
}