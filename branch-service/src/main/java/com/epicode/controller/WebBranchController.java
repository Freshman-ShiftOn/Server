package com.epicode.controller;

import com.epicode.dto.BranchRequestDTO;
import com.epicode.dto.WorkerDTO;
import com.epicode.dto.WorkerRequestDTO;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.model.Branch;
import com.epicode.repository.UserBranchRepository;
import com.epicode.repository.UserRepository;
import com.epicode.security.InviteToken;
import com.epicode.service.BranchService;
import com.epicode.service.InviteService;
import com.epicode.service.S3Service;
import com.epicode.service.UserBranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    private final UserRepository userRepository;

    @Operation(
            summary = "(사장님) 매장 근무자 추가",
            description = "매장 근무자를 추가합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI..."),
                    @Parameter(name = "WorkerRequestDTO", description = "근무자", required = true)
            }
    )
    @PostMapping("/workers")
    public ResponseEntity<?> AddWorkers(
            @RequestBody WorkerRequestDTO dto) {
        userBranchService.addUserAtBranch(dto);
        return ResponseEntity.ok().build();
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
            @org.springframework.web.bind.annotation.RequestBody Branch branch,
            @RequestHeader("X-Authenticated-User") String email
    ) {
        Long userId = userRepository.findIdByEmail(email).getId();
        BranchRequestDTO dto = new BranchRequestDTO();
        dto.setName(branch.getName());
        dto.setAdress(branch.getAdress());
        dto.setDial_numbers(branch.getDial_numbers());
        dto.setBasic_cost(branch.getBasic_cost());
        dto.setWeekly_allowance(branch.getWeekly_allowance());
        dto.setUserId(userId);
        dto.setEmail(email);
        branchService.createBranch(dto);
        return ResponseEntity.ok().build();
    }



}
