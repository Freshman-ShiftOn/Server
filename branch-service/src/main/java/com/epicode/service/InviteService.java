package com.epicode.service;

public interface InviteService {
//    /**
//     * 초대 이메일과 인증된 이메일을 검증하고 지점에 가입 처리.
//     *
//     * @param inviteEmail       초대받은 이메일
//     * @param inviteBranchId    초대받은 지점 ID
//     * @param authenticatedEmail 현재 인증된 이메일
//     */
    void validateAndJoinBranch(Long inviteBranchId, String authenticatedEmail);
    //String generateInviteToken(String inviterEmail, String inviteeEmail, Long branchId);

    String generateInviteToken(String inviterEmail, Long branchId);
}