package com.epicode.service;

import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.model.UserBranch;
import com.epicode.repository.BranchRepository;
import com.epicode.repository.UserBranchRepository;
import com.epicode.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public String deleteUserBranchByEmailAndBranchId(String email, Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new CustomException(ErrorCode.BRANCH_NOT_FOUND));

        //유저가 해당 브랜치에 속해 있는지 검증
        User user = userRepository.findIdByEmail(email);
        UserBranch userBranch = userBranchRepository.findByUserAndBranch(user, branch)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_BRANCH_NOT_FOUND));

        //해당 유저-브랜치 가입 정보 삭제
        userBranchRepository.deleteByUserAndBranch(user, branch);
        //해당 유저에게서 삭제된 브랜치 이름 반환
        return branch.getName();
    }
}