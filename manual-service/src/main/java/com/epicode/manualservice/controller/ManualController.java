package com.epicode.manualservice.controller;

import com.epicode.manualservice.dto.ManualDTO;
import com.epicode.manualservice.dto.ManualTaskDTO;
import com.epicode.manualservice.exception.BranchAuthorizeException;
import com.epicode.manualservice.service.ManualService;
import com.epicode.manualservice.service.ManualTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/manuals")
@RequiredArgsConstructor
@Tag(name = "manual-service-controller", description = "Manual 서비스 API")
public class ManualController {
    private final Environment env;
    private final ManualService manualService;
    private final ManualTaskService manualTaskService;

    // 선택한 매장의 매뉴얼 목록 조회
    @GetMapping("/{branchId}")
    @Operation(
            summary = "매뉴얼 목록 조회",
            description = "사용자가 접근 권한이 있는 매장에서 모든 매뉴얼 목록을 조회합니다.",
            parameters = {
                    @Parameter(name = "branchId", description = "조회할 매장의 ID", required = true, example = "101"),
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI...")
            }
    )
    public ResponseEntity<List<ManualDTO>> getManualsByBranchId(
            @RequestHeader("X-Branch-Ids") String branches,
            @PathVariable Integer branchId) {
        System.out.println("Service received branchId: " + branchId);
        if (branchId == null) {
            throw new IllegalArgumentException("branchId is required and must be a number.");
        }
        manualService.validateBranchAccess(branches, branchId);
        List<ManualDTO> manuals = manualService.getManualsByBranchId(branchId);
        return ResponseEntity.ok(manuals);
    }

    // 매뉴얼 상세 조회 및 태스크 포함
    @GetMapping("/{branchId}/{id}")
    @Operation(
            summary = "매뉴얼 상세 조회",
            description = "사용자가 접근 권한이 있는 매장에서 특정 매뉴얼의 상세 정보를 조회하고, 관련 태스크를 반환합니다.",
            parameters = {
                    @Parameter(name = "branchId", description = "매장의 ID", required = true, example = "101"),
                    @Parameter(name = "id", description = "조회할 매뉴얼의 ID", required = true, example = "123"),
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI...")
            }
    )
    public ResponseEntity<ManualDTO> getManualWithTasks(
            @RequestHeader("X-Branch-Ids") String branches,
            @PathVariable Integer branchId,
            @PathVariable Integer id) {
        manualService.validateBranchAccess(branches, branchId);
        ManualDTO manual = manualService.getManualByIdAndBranchId(id, branchId);
        List<ManualTaskDTO> tasks = manualTaskService.getManualTasksByManualId(id);
        manual.setTasks(tasks);
        return ResponseEntity.ok(manual);
    }

    // 매뉴얼 생성
    @PostMapping("/{branchId}")
    @Operation(
            summary = "매뉴얼 생성(지점ID,유저ID는 manualDTO 요청 시 제외하기)",
            description = "사용자가 접근 권한이 있는 매장에 새로운 매뉴얼을 생성합니다.",
            parameters = {
                    @Parameter(name = "branchId", description = "매장의 ID", required = true, example = "101"),
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI...")
            }
    )
    public ResponseEntity<ManualDTO> createManual(
            @RequestHeader("X-Branch-Ids") String branches,
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Integer branchId,
            @RequestBody ManualDTO manualDTO) {
        manualService.validateBranchAccess(branches, branchId);
        manualDTO.setBranchId(branchId);
        manualDTO.setWorkerId(Integer.valueOf(userId));
        ManualDTO createdManual = manualService.createManual(manualDTO);
        return ResponseEntity.ok(createdManual);
    }

    // 매뉴얼 수정
    @PutMapping("/{branchId}/{id}")
    @Operation(
            summary = "매뉴얼 수정(지점ID,유저ID는 manualDTO 요청 시 제외하기)",
            description = "사용자가 접근 권한이 있는 매장에서 특정 매뉴얼을 수정합니다.",
            parameters = {
                    @Parameter(name = "branchId", description = "매장의 ID", required = true, example = "101"),
                    @Parameter(name = "id", description = "수정할 매뉴얼의 ID", required = true, example = "123"),
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI...")
            }
    )
    public ResponseEntity<ManualDTO> updateManual(
            @RequestHeader("X-Branch-Ids") String branches,
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Integer branchId,
            @PathVariable Integer id,
            @RequestBody ManualDTO manualDTO) {
        manualService.validateBranchAccess(branches, branchId);
        manualDTO.setBranchId(branchId);
        manualDTO.setWorkerId(Integer.valueOf(userId));
        ManualDTO updatedManual = manualService.updateManual(id, manualDTO);
        return ResponseEntity.ok(updatedManual);
    }

    // 매뉴얼 삭제
    @DeleteMapping("/{branchId}/{id}")
    @Operation(
            summary = "매뉴얼 삭제",
            description = "사용자가 접근 권한이 있는 매장에서 특정 매뉴얼을 삭제합니다.",
            parameters = {
                    @Parameter(name = "branchId", description = "매장의 ID", required = true, example = "101"),
                    @Parameter(name = "id", description = "삭제할 매뉴얼의 ID", required = true, example = "123"),
                    @Parameter(name = "Authorization", description = "JWT Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJI...")
            }
    )
    public ResponseEntity<Void> deleteManual(
            @RequestHeader("X-Branch-Ids") String branches,
            @PathVariable Integer branchId,
            @PathVariable Integer id) {
        manualService.validateBranchAccess(branches, branchId);
        manualService.deleteManual(id);
        return ResponseEntity.noContent().build();
    }
}
