package com.epicode.controller;

import com.epicode.dto.BranchIdNameProjection;
import com.epicode.dto.WorkerProjection;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.model.Branch;
import com.epicode.repository.UserRepository;
import com.epicode.security.InviteToken;
import com.epicode.service.BranchService;
import com.epicode.service.InviteService;
import com.epicode.service.S3Service;
import com.epicode.service.UserBranchService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RequestMapping({"/api/branch"})
@Tag(
        name = "user branch-service-controller",
        description = "Branch 서비스 API"
)
@RestController
@RequiredArgsConstructor
public class UserBranchController {
    private final UserBranchService userBranchService;

    @Operation(
            summary = "사용자 지점 탈퇴",
            description = "특정 지점을 탈퇴합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI..."),
                    @Parameter(name = "BranchId", description = "지점 ID", required = true)
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
}
