package com.epicode.controller;

import com.epicode.exception.NoBranchFoundException;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.repository.UserRepository;
import com.epicode.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final UserRepository userRepository;


    @GetMapping({"/list"})
    @Operation(
            summary = "사용자 지점 가입 목록 조회",
            description = "사용자가 가입한 매장 목록을 조회한다."
    )
    public List<String> getBranchNames(
            @Parameter(
                    name = "X-Branch-Ids",
                    description = "사용자의 지점 가입 정보",
                    required = true,
                    example = "[101,202]"
            )
            @RequestHeader("X-Authenticated-User") String email,
            @RequestHeader("X-Branch-Ids") String branches // 문자열로 받음
    ) {
        // 사용자 검증
        User user = userRepository.findIdByEmail(email);
        List<String> branchNames = new ArrayList<>();
        if (user == null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다.");
        } else {
            //List<String> branchNames = branchService.getBranchNamesByUserId(userId);
            Long[] branchIds = Arrays.stream(branches.split(",")) // ','로 분리
                    .map(Long::valueOf) // Long으로 변환
                    .toArray(Long[]::new);
            branchNames = branchService.getBranchNamesByUserIds(branchIds);
            if (branchNames == null || branchNames.isEmpty()) {
                throw new NoBranchFoundException("가입된 지점이 없습니다.");
            }
        }
        return branchNames;
    }

    @GetMapping({"/{branchName}"})
    @Operation(
            summary = "특정 매장 조회",
            description = "해당 매장 이름을 선택하면 branchId가 리턴된다."
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
            return branchService.getBranchIdByName(branchName);
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
        Long userId = userRepository.findIdByEmail(email).getId();
        branchService.createBranch(branch, userId, email);
        return ResponseEntity.ok().build();
    }
}