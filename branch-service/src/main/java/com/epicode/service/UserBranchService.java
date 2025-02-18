package com.epicode.service;

public interface UserBranchService {
    void joinBranch(Long userId, Long branchId);
    String deleteUserBranchByEmailAndBranchId(String email, Long branchId);
}