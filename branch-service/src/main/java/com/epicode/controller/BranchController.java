package com.epicode.controller;

import com.epicode.exception.NoBranchFoundException;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.repository.UserRepository;
import com.epicode.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping({"/api/branch"})
@Tag(
        name = "branch-service-controller",
        description = "Branch 서비스 API"
)
@RestController
public class BranchController {

    private final BranchService branchService;
    private final UserRepository userRepository;

    public BranchController(final BranchService branchService, final UserRepository userRepository) {
        this.branchService = branchService;
        this.userRepository = userRepository;
    }

    @GetMapping({"/list"})
    @Operation(
            summary = "사용자 지점 가입 목록 조회",
            description = "사용자가 가입한 매장 목록을 조회한다."
    )
    public List<String> getBranchNames(
            @Parameter(
                    name = "X-Authenticated-User",
                    description = "사용자의 인증된 이메일 주소",
                    required = true,
                    example = "user@example.com"
            )
            @RequestHeader("X-Authenticated-User") String email
    ) {
        User user = this.userRepository.findIdByEmail(email);
        Long userId = user.getId();
        if (user == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다.");
        } else {
            List<String> branchNames = this.branchService.getBranchNamesByUserId(userId);
            if (branchNames != null && !branchNames.isEmpty()) {
                return branchNames;
            } else {
                throw new NoBranchFoundException("가입된 지점이 없습니다.");
            }
        }
    }

    @GetMapping({"/{branchName}"})
    @Operation(
            summary = "특정 매장 조회",
            description = "해당 매장을 선택하면 branchId가 리턴된다."
    )
    public Long getBranchIdByName(
            @Parameter(
                    name = "branchName",
                    description = "조회할 매장의 이름",
                    required = true,
                    example = "Main Branch"
            )
            @PathVariable String branchName
    ) {
        if (branchName == null) {
            throw new IllegalArgumentException("Branch name is required.");
        } else {
            return this.branchService.getBranchIdByName(branchName);
        }
    }

    @PostMapping({"/create"})
    @Operation(
            summary = "지점 추가",
            description = "매장을 새로 생성한다."
    )
    public ResponseEntity<Void> createBranch(
            @Parameter(
                    description = "생성할 매장의 정보",
                    required = true
            )
            @RequestBody Branch branch,
            @Parameter(
                    name = "X-Authenticated-User",
                    description = "사용자의 인증된 이메일 주소",
                    required = true,
                    example = "user@example.com"
            )
            @RequestHeader("X-Authenticated-User") String email
    ) {
        Long userId = this.userRepository.findIdByEmail(email).getId();
        this.branchService.createBranch(branch, userId, email);
        return ResponseEntity.ok().build();
    }
}