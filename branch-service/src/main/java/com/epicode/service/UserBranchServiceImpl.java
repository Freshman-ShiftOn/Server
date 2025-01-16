package com.epicode.service;

import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.model.UserBranch;
import com.epicode.repository.BranchRepository;
import com.epicode.repository.UserBranchRepository;
import com.epicode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBranchServiceImpl implements UserBranchService {

    private final UserBranchRepository userBranchRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @Override
    public void joinBranch(Long userId, Long branchId) {
        //입력값 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new CustomException(ErrorCode.BRANCH_NOT_FOUND));
        //이미 가입 여부 확인
        if (userBranchRepository.existsByUserIdAndBranchId(userId, branchId)) {
            throw new CustomException(ErrorCode.USER_BRANCH_EXISTS);
        }

        UserBranch userBranch = new UserBranch();
        userBranch.setUser(user);
        userBranch.setBranch(branch);
        userBranch.setJoinedAt(java.time.LocalDateTime.now());
        userBranchRepository.save(userBranch);
    }
}