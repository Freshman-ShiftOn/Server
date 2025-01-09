package com.epicode.controller;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.model.UserBranch;
import com.epicode.repository.UserRepository;
import com.epicode.service.BranchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
        //Branch 목록 반환
        return branchService.getBranchNamesByUserId(Long.valueOf(userId));
    }
    @GetMapping("/{branchName}")
    public Long getBranchIdByName(@PathVariable String branchName) {
        return branchService.getBranchIdByName(branchName);
    }
}