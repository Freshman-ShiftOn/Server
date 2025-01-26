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
import com.epicode.service.UserBranchService;
import io.jsonwebtoken.Claims;
import com.epicode.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequestMapping({"/api/branch"})
@Tag(
        name = "branch-service-controller",
        description = "Branch 서비스 API"
)
@RestController
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;
    private final UserBranchService userBranchService;
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final InviteService inviteService;
    private final InviteToken inviteTokenizer;

    @GetMapping({"/list"})
    @Operation(
            summary = "사용자 지점 가입 목록 조회",
            description = "사용자가 가입한 매장 목록을 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI...")
            }
    )
    public List<BranchIdNameProjection> getBranchNames(
            @RequestHeader("X-Authenticated-User-Id") String userId
    ) {
        // 사용자의 Branch 정보를 가져오기
        List<BranchIdNameProjection> branchNames = branchService.getBranchesByUserId(Long.valueOf(userId));
        return branchNames != null ? branchNames : List.of();
    }
//    public List<String> getBranchNames(
//            @RequestHeader("X-Authenticated-User-Id") String userId
//    ) {
//        // 사용자의 Branch 정보를 가져오기
//        List<String> branchNames = branchService.getBranchNamesByUserId(Long.valueOf(userId));
//        return branchNames != null ? branchNames : List.of();
////        // 사용자 검증
////        User user = userRepository.findIdByEmail(email);
////        List<String> branchNames = new ArrayList<>();
////        if (user == null) {
////            throw new IllegalArgumentException("해당하는 사용자가 없습니다.");
////        } else {
////            Long[] branchIds = Arrays.stream(branches.split(","))
////                    .map(Long::valueOf)
////                    .toArray(Long[]::new);
////            branchNames = branchService.getBranchNamesByUserIds(branchIds);
////            if (branchNames == null || branchNames.isEmpty()) {
////                //throw new NoBranchFoundException("가입된 지점이 없습니다.");
////                return new ArrayList<>();
////            }
////        }
////        return branchNames;
//    }

    @GetMapping({"/search/{branchName}"})
    @Operation(
            summary = "특정 매장 조회",
            description = "해당 매장 이름을 선택하면 branchId가 반환됩니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI..."),
                    @Parameter(name = "branchName", description = "조회할 매장의 이름", required = true, example = "Main Branch")
            }
    )
    public Long getBranchIdByName(
            @PathVariable String branchName
    ) {
        if (branchName == null) {
            throw new CustomException(ErrorCode.INVALID_BRANCH_NAME);
        } else {
            return branchService.getBranchIdByName(branchName);
        }
    }

    @PostMapping({"/create-branch"})
    @Operation(
            summary = "지점 추가",
            description = "새로운 매장을 생성합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI..."),
                    @Parameter(name = "branch", description = "생성할 매장의 정보", required = true)
            }
    )
    public ResponseEntity<Void> createBranch(
            @RequestBody Branch branch,
            @RequestHeader("X-Authenticated-User") String email
    ) {
        Long userId = userRepository.findIdByEmail(email).getId();
        branchService.createBranch(branch, userId, email);
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "매장 근무 동료들 조회",
            description = "해당 매장에 근무하는 근무자의 id,name이 반환됩니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI..."),
                    @Parameter(name = "branchId", description = "조회할 매장Id", required = true, example = "101")
            }
    )
    @GetMapping("/{branchId}/workers")
    public List<WorkerProjection> getWorkersByBranchId(@PathVariable Long branchId) {
        return branchService.getWorkersByBranchId(branchId);
    }


    @Operation(
            summary = "특정 매장 프로필 조회",
            description = "해당 매장의 프로필(이미지,지점 정보)이 리턴됩니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI..."),
                    @Parameter(name = "branchId", description = "조회할 매장Id", required = true, example = "101")
            }
    )
    @GetMapping("/{branchId}/profile")
    public ResponseEntity<Branch> getBranchProfile(@PathVariable Long branchId) {
        Branch branch = branchService.getBranchProfile(branchId);
        return ResponseEntity.ok(branch);
    }

    @PostMapping("/upload")
    @Operation(summary = "이미지 업로드", description = "이미지를 S3에 업로드하고 URL을 반환합니다.")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            // 파일 검증 (유형 및 크기 제한)
            if (!image.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Invalid file type. Only images are allowed.");
            }
            if (image.getSize() > 5 * 1024 * 1024) { // 5MB 제한
                return ResponseEntity.badRequest().body("File size exceeds the 5MB limit.");
            }

            // S3 업로드 처리
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            File tempFile = File.createTempFile("upload-", fileName);
            image.transferTo(tempFile);
            String imageUrl = s3Service.uploadFile("branches/" + fileName, tempFile);

            // 성공적으로 업로드된 이미지 URL 반환
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed.");
        }
    }

    @Operation(
            summary = "사용자 지점 가입",
            description = "사용자가 초대장 토큰을 가지고 특정 지점(branchId)에 가입합니다.",
            parameters = {
                    @Parameter(name = "inviteToken", description = "초대장 토큰", required = true, example = "eyJhbGciOiJIUzI1NiJ9..."),
                    @Parameter(name = "branchId", description = "지점 ID", required = true, example = "101")
            }
    )
    @PostMapping("/invite/join")
    public ResponseEntity<Void> joinBranch(
            @RequestHeader("X-Authenticated-User") String email,
            @RequestParam String token
    ) {
        Claims claims = inviteTokenizer.parseInviteToken(token);
        //String inviteeEmail = claims.get("email", String.class);
        //String inviteeEmail = inviteTokenizer.parseInviteToken(token).get("email", String.class);
        //Long branchId = inviteTokenizer.parseInviteToken(token).get("branchId", Long.class);
        Long branchId = Long.valueOf(claims.get("branchId", String.class));//String으로 받아 형 변환(혹시 몰라)
        inviteService.validateAndJoinBranch(branchId,email);//검증&가입
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "초대장 생성",
            description = "초대자 이메일, 초대받는 사람 이메일, 지점 ID를 받아 초대장 토큰을 생성합니다.",
            parameters = {
                    @Parameter(name = "inviterEmail", description = "초대자 이메일", required = true, example = "inviter@example.com"),
                    //@Parameter(name = "inviteeEmail", description = "초대받는 사람 이메일", required = true, example = "invitee@example.com"),
                    @Parameter(name = "branchId", description = "초대 지점 ID", required = true, example = "101")
            }
    )
    @GetMapping("/invite/generate")
    public ResponseEntity<String> generateInviteToken(
            @RequestParam String inviterEmail,
            //@RequestParam String inviteeEmail,
            @RequestParam Long branchId
    ) {
        String inviteToken = inviteService.generateInviteToken(inviterEmail, branchId);
        return ResponseEntity.ok(inviteToken);
    }
}
