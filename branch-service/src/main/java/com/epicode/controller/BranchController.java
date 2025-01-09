package com.epicode.controller;
import com.epicode.exception.NoBranchFoundException;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.repository.UserRepository;
import com.epicode.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/branch")
@RequiredArgsConstructor
@Tag(name = "branch-service-controller", description = "Branch 서비스 API")
@RestController
public class BranchController {
    private final BranchService branchService;
    private final UserRepository userRepository;

    @GetMapping("/list")
    @Operation(summary = "사용자 지점 가입 목록 조회", description = "사용자가 가입한 매장 목록을 조회한다.")
    public List<String> getBranchNames(@RequestHeader("X-Authenticated-User") String email) {
        User user = userRepository.findIdByEmail(email);
        Long userId = user.getId();
        if (user==null) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다.");
        }
        //Branch 목록 가져오기
        List<String> branchNames = branchService.getBranchNamesByUserId(userId);

        // Branch 목록이 비어 있는 경우 예외 처리
        if (branchNames == null || branchNames.isEmpty()) {
            throw new NoBranchFoundException("가입된 지점이 없습니다.");
        }
        return branchNames;
    }
    @GetMapping(value = {"/{branchName}", "/"})
    @Operation(summary = "특정 매장 조회", description = "해당 매장을 선택하면 branchId가 리턴된다.")
    public Long getBranchIdByName(@PathVariable(required = false) String branchName) {
        if (branchName == null) {
            throw new IllegalArgumentException("Branch name is required.");
        }
        return branchService.getBranchIdByName(branchName);
    }
    @PostMapping("/create")
    @Operation(summary = "지점 추가", description = "매장을 새로 생성한다.")
    public ResponseEntity<Void> createBranch(@RequestBody Branch branch,
                                             @RequestHeader("X-Authenticated-User") String email) {
        Long userId = userRepository.findIdByEmail(email).getId();
        branchService.createBranch(branch, userId, email);
        return ResponseEntity.ok().build();//200
    }
}