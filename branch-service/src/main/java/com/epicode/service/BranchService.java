package com.epicode.service;
import com.epicode.model.Branch;
import com.epicode.model.UserBranch;

import java.util.List;

public interface BranchService {
    List<String> getBranchNamesByUserId(Long userId);

    Long getBranchIdByName(String name);
    void createBranch(Branch branch, Long userId, String email);
}