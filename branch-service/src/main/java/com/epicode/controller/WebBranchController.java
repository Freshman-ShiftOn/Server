package com.epicode.controller;

import com.epicode.dto.WorkerDTO;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.repository.UserBranchRepository;
import com.epicode.repository.UserRepository;
import com.epicode.security.InviteToken;
import com.epicode.service.BranchService;
import com.epicode.service.InviteService;
import com.epicode.service.S3Service;
import com.epicode.service.UserBranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping({"/api/branch"})
@Tag(
        name = "web branch-service-controller",
        description = "(Web) Branch 서비스 API"
)
@RestController
@RequiredArgsConstructor
public class WebBranchController {
    private final UserBranchService userBranchService;
    private final BranchService branchService;
    private final UserBranchRepository userBranchRepository;

    @Operation(
            summary = "(사장님) 매장 근무자 추가",
            description = "매장 근무자를 추가합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI..."),
                    @Parameter(name = "BranchId", description = "지점 ID", required = true),
                    @Parameter(name = "Worker", description = "근무자", required = true)
            }
    )
    // 특정 유저의 특정 브랜치 탈퇴
    @DeleteMapping("/signout/{branchId}")
    public ResponseEntity<String> deleteUserBranch(@RequestHeader("X-Authenticated-User") String email, @PathVariable Long branchId) {
        try {
            String branchName = userBranchService.deleteUserBranchByEmailAndBranchId(email, branchId);
            return ResponseEntity.ok("Branch '" + branchName + "' removed successfully for user: " + email);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(e.getMessage());
        }
    }

    @Operation(
            summary = "매장 근무 동료들 조회",
            description = "해당 매장에 근무하는 근무자의 이름,역할,번호,시급,상태가 반환됩니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI..."),
                    @Parameter(name = "branchId", description = "조회할 매장Id", required = true, example = "101")
            }
    )
    @GetMapping("/{branchId}/workers")
    public List<WorkerDTO> getWorkersByBranchId(
            @PathVariable Long branchId,
            @RequestHeader("X-Authenticated-User-Id") String userId) {
        List<Long> branchList = userBranchRepository.findBranchIdsByUserId(Long.valueOf(userId));
        boolean exist = false;
        for(Long l:branchList){
            if(l==branchId) exist = true;
        }
        if(!exist) throw new CustomException(ErrorCode.USER_BRANCH_NOT_FOUND);
        return branchService.getWorkersByBranchId(branchId);
    }


}
