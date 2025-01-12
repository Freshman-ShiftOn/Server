package com.epicode.controller;
import com.epicode.domain.Salary;
import com.epicode.dto.SalaryRequestDTO;
import com.epicode.dto.SalaryResponseDTO;
import com.epicode.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
@Tag(name = "급여 관리 API", description = "지점 별 사용자 급여와 관련된 API")
public class SalaryController {
    private final SalaryService salaryService;
    //없으면 생성 or 있으면 수정
    @Operation(
            summary = "급여 생성 또는 수정",
            description = "사용자와 지점 정보를 기반으로 급여 데이터를 생성하거나, 기존 데이터를 수정합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIs...")
            }
    )
    @PostMapping
    public ResponseEntity<Void> createOrUpdateSalary(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @RequestBody SalaryRequestDTO salaryRequestDTO) {
        salaryService.createOrUpdateSalary(Long.valueOf(userId), salaryRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //해당 지점의 내 급여 조회
    @Operation(
            summary = "내 급여 조회",
            description = "특정 지점에서 사용자의 급여 데이터를 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIs..."),
                    @Parameter(name = "branchId", description = "조회할 지점의 ID", required = true, example = "101")
            }
    )
    @GetMapping("/{branchId}")
    public ResponseEntity<Salary> getSalaryByUserAndBranch(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long branchId) {
        Salary salary = salaryService.getSalaryByUserAndBranch(Long.valueOf(userId), branchId);
        return ResponseEntity.ok(salary);
    }

    //해당 지점의 모든 급여 설정 조회

    @Operation(
            summary = "지점의 모든 급여 조회",
            description = "특정 지점의 모든 급여 데이터를 모두 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIs..."),
                    @Parameter(name = "branchId", description = "조회할 지점의 ID", required = true, example = "101")
            }
    )
    @GetMapping("/all/{branchId}")//나중에 인가적용 필요
    public ResponseEntity<List<SalaryResponseDTO>> getSalariesByBranch(@PathVariable Long branchId) {
        List<SalaryResponseDTO> salaries = salaryService.getSalariesByBranch(branchId);
        return ResponseEntity.ok(salaries);
    }

    //해당 지점의 내 급여 삭제
    @Operation(
            summary = "급여 삭제",
            description = "특정 지점에서 사용자의 급여 데이터를 삭제합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIs..."),
                    @Parameter(name = "branchId", description = "삭제할 지점의 ID", required = true, example = "101")
            }
    )
    @DeleteMapping("/{branchId}")
    public ResponseEntity<Void> deleteSalary(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long branchId) {
        salaryService.deleteSalary(branchId, Long.valueOf(userId));
        return ResponseEntity.noContent().build();
    }


//    @PostMapping
//    public ResponseEntity<Void> createSalary(
//            @RequestHeader("X-Authenticated-User-Id") String userId,
//            @RequestBody SalaryRequestDTO salaryRequestDTO) {
//        salaryService.createSalary(Long.valueOf(userId),salaryRequestDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//    //해당 지점의 내 급여 수정
//    @PutMapping("/{branchId}")
//    public ResponseEntity<Void> updateSalary(
//            @RequestHeader("X-Authenticated-User-Id") String userId,
//            @PathVariable Long branchId,
//            @RequestBody SalaryRequestDTO salaryRequestDTO) {
//        salaryService.updateSalary(Long.valueOf(userId), branchId, salaryRequestDTO);
//        return ResponseEntity.noContent().build();
//    }
}