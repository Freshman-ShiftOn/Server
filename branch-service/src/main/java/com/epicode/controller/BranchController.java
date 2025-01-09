package com.epicode.controller;
import com.epicode.exception.NoBranchFoundException;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.model.UserBranch;
import com.epicode.repository.UserRepository;
import com.epicode.service.BranchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
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
    public List<String> getBranchNames(@RequestHeader("X-Authenticated-User") String email,
                                       @RequestHeader("X-User-Id") String userId) {
        User user = userRepository.findIdByEmail(email);
        if (!user.getId().equals(Long.valueOf(userId))) {
            throw new IllegalArgumentException("User ID does not match the authenticated email.");
        }
        //Branch 목록 가져오기
        List<String> branchNames = branchService.getBranchNamesByUserId(Long.valueOf(userId));

        // Branch 목록이 비어 있는 경우 예외 처리
        if (branchNames == null || branchNames.isEmpty()) {
            throw new NoBranchFoundException("가입된 지점이 없습니다.");
        }
        return branchNames;
    }
    @GetMapping(value = {"/{branchName}", "/"})
    public Long getBranchIdByName(@PathVariable(required = false) String branchName) {
        if (branchName == null) {
            throw new IllegalArgumentException("Branch name is required.");
        }
        return branchService.getBranchIdByName(branchName);
    }
    @PostMapping("/create")
    public ResponseEntity<Void> createBranch(@RequestBody Branch branch,
                                             @RequestHeader("X-User-Id") String userId,
                                             @RequestHeader("X-Authenticated-User") String email) {
        branchService.createBranch(branch, Long.valueOf(userId), email);
        return ResponseEntity.ok().build();//200
    }
}