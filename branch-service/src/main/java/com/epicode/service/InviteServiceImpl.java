package com.epicode.service;

import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.model.UserBranch;
import com.epicode.repository.UserRepository;
import com.epicode.security.InviteToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
    private final UserBranchService userBranchService;
    private final UserRepository userRepository;
    private final InviteToken inviteToken;

    @Override
    public void validateAndJoinBranch(Long inviteBranchId, String authenticatedEmail) {
//        // 초대받은 이메일과 현재 인증된 이메일 비교
//        if (!inviteEmail.equals(authenticatedEmail)) {
//            throw new CustomException(ErrorCode.USER_NOT_INVITED);
//        }
        Long authenticatedUserId = userRepository.findIdByEmail(authenticatedEmail).getId();
        //토큰 값 기반, 초대 인증 된 사용자 -  지점 가입 처리
        userBranchService.joinBranch(authenticatedUserId, inviteBranchId);
    }

    @Override
    public String generateInviteToken(String inviterEmail, Long branchId) {
//        // 초대자 이메일이 UserRepository에 존재하는지 확인
//        if (userRepository.findIdByEmail(inviterEmail) == null) {
//            throw new CustomException(ErrorCode.USER_NOT_FOUND);
//        }
        return inviteToken.generateInviteToken(inviterEmail, branchId);
    }
}