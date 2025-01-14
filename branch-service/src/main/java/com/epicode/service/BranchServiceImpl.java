package com.epicode.service;
import com.epicode.dto.WorkerProjection;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.model.UserBranch;
import com.epicode.repository.BranchRepository;
import com.epicode.repository.UserBranchRepository;
import com.epicode.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final UserBranchRepository userBranchRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    // userId 기반 branchName 조회
    public List<String> getBranchNamesByUserId(Long userId) {
        //UserBranch에서 branchId 목록 조회
        List<Long> branchIds = userBranchRepository.findBranchIdsByUserId(userId);
        //Branch에서 branchName 목록 조회
        return branchRepository.findBranchNamesByIds(branchIds);
    }
    //branchIds기반 branchName 조회
    public List<String> getBranchNamesByUserIds(Long[] branchIds) {
        //branchIds -> branchName 목록 조회
        return branchRepository.findBranchNamesByIds(List.of(branchIds));
    }
    // branchName 기반 branchId 조회
    public Long getBranchIdByName(String name) {
        Branch b = branchRepository.findIdByName(name);
        return b.getId();
    }

    @Transactional
    public void createBranch(Branch branch, Long userId, String email) {
        User user = userRepository.findIdByEmail(email);

        if (user == null || !user.getId().equals(userId)) {
            throw new IllegalArgumentException("헤더값을 확인하세요.");
        }

        //Branch 저장
        Branch savedBranch = branchRepository.save(branch);

        UserBranch userBranch = new UserBranch();
        userBranch.setUser(user);
        userBranch.setBranch(savedBranch);
        userBranch.setJoinedAt(LocalDateTime.now());

        //UserBranch 저장
        userBranchRepository.save(userBranch);
    }

    public List<WorkerProjection> getWorkersByBranchId(Long branchId) {
        if (branchId == null) {
            throw new IllegalArgumentException("Branch ID must not be null");
        }
        return userBranchRepository.findWorkersByBranchId(branchId);
    }
}
