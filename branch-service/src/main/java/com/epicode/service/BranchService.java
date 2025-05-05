package com.epicode.service;
import com.epicode.dto.BranchIdNameProjection;
import com.epicode.dto.WorkerProjection;
import com.epicode.model.Branch;
import com.epicode.model.UserBranch;

import java.util.List;

public interface BranchService {
    //List<String> getBranchNamesByUserId(Long userId);//브랜치 네임 가져오기(ver1)
    //List<String> getBranchNamesByUserIds(Long[] branchIds);
    Long getBranchIdByName(String name);
    void createBranch(Branch branch, Long userId, String email);
    List<WorkerProjection> getWorkersByBranchId(Long branchId);
    Branch getBranchProfile(Long branchId);
    String getBranchNameByBranchId(Long branchId);
    List<BranchIdNameProjection> getBranchesByUserId(Long userId);//브랜치id,name 가져오기(ver2)
}