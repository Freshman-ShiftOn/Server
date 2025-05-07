package com.epicode.controller;

import com.epicode.dto.SalaryRequestDTO;
import com.epicode.dto.SalaryResponseDTO;
import com.epicode.dto.SpecificTimeSalaryResponseDTO;
import com.epicode.repository.SalaryRepository;
import com.epicode.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
@Tag(name = "급여 관리 API", description = "사용자 기본 시급 및 특별 시급 관련 API를 제공합니다.")
public class SalaryController {
    private final SalaryService salaryService;
    private final SalaryRepository salaryRepository;
    @Operation(
            summary = "해당 매장에 사용자 급여 정보 존재 여부 확인",
            description = "사용자의 급여 정보가 존재하는지 확인합니다.(true/false)",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1Ni..."),
                    @Parameter(name = "branchId", description = "조회할 지점 ID", required = true, example = "101")
            }
    )
    @GetMapping("/{branchId}/check")
    public boolean checkSalaryExists(@RequestHeader("X-Authenticated-User-Id") String userId, @PathVariable Long branchId) {
        return salaryRepository.existsByUserIdAndBranchId(Long.valueOf(userId), branchId);
    }

    @Operation(
            summary = "급여 생성/수정",
            description = "사용자의 기본 시급과 특별 시급 정보를 생성하거나 수정합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1Ni...")
            }
    )
    @PostMapping
    public ResponseEntity<SalaryResponseDTO> createOrUpdateSalary(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @RequestBody SalaryRequestDTO salaryRequestDTO) {
        SalaryResponseDTO response = salaryService.createOrUpdateSalary(Long.valueOf(userId), salaryRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "기본 및 특별 시급 조회",
            description = "사용자의 기본 시급과 특별 시급 정보를 함께 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1Ni..."),
                    @Parameter(name = "branchId", description = "조회할 지점 ID", required = true, example = "101")
            }
    )
    @GetMapping("/{branchId}")
    public ResponseEntity<SalaryResponseDTO> getSalaryByUserAndBranch(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long branchId) {
        SalaryResponseDTO response = salaryService.getSalaryByUserAndBranch(Long.valueOf(userId), branchId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "기본 시급 조회",
            description = "사용자의 기본 시급 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1Ni..."),
                    @Parameter(name = "branchId", description = "조회할 지점 ID", required = true, example = "101")
            }
    )
    @GetMapping("/{branchId}/basic")
    public ResponseEntity<SalaryResponseDTO> getBasicSalary(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long branchId) {
        SalaryResponseDTO response = salaryService.getBasicSalary(Long.valueOf(userId), branchId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "특별 시급 조회",
            description = "사용자의 특정 시간대에 적용되는 특별 시급 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1Ni..."),
                    @Parameter(name = "branchId", description = "조회할 지점 ID", required = true, example = "101")
            }
    )
    @GetMapping("/{branchId}/specific")
    public ResponseEntity<List<SpecificTimeSalaryResponseDTO>> getSpecificSalaries(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long branchId) {
        List<SpecificTimeSalaryResponseDTO> response = salaryService.getSpecificSalaries(Long.valueOf(userId), branchId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "특별 시급 삭제",
            description = "특정 사용자와 지점에 연결된 특정 특별 시급 데이터를 삭제합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1Ni..."),
                    @Parameter(name = "branchId", description = "삭제할 지점 ID", required = true, example = "101"),
                    @Parameter(name = "specificTimeSalaryId", description = "삭제할 특별 시급 ID", required = true, example = "1")
            }
    )
    @DeleteMapping("/{branchId}/specific/{specificTimeSalaryId}")
    public ResponseEntity<Void> deleteSpecificTimeSalary(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long branchId,
            @PathVariable Long specificTimeSalaryId) {
        salaryService.deleteSpecificTimeSalary(Long.valueOf(userId), branchId, specificTimeSalaryId);
        return ResponseEntity.noContent().build();
    }


    

}
