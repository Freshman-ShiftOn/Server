package com.epicode.controller;

import com.epicode.dto.WeeklySalaryDto;
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
@Tag(name = "급여 관리 Web API", description = "웹 사용자 기본 시급 및 특별 시급 관련 API를 제공합니다.")
public class SalaryWebController {
    private final SalaryService salaryService;

    @Operation(
            summary = "월간 주별 급여 조회",
            description = "지점(branchId), 월(month), 주차(week)에 해당하는 주간 급여 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1Ni..."),
                    @Parameter(name = "branchId", description = "지점 ID", required = true, example = "101"),
                    @Parameter(name = "month", description = "조회할 월", required = true, example = "5")
            }
    )
    @GetMapping("/{branchId}/{month}/week")
    public ResponseEntity<?> getWeeklySalary(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long branchId,
            @PathVariable int month
    ) {
        List<WeeklySalaryDto> salary = salaryService.getSalaryInfo(branchId, month);
        return ResponseEntity.ok(salary);
    }

    @Operation(
            summary = "월간 급여 조회",
            description = "지점(branchId), 월(month)에 해당하는 주간 급여 정보를 총 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1Ni..."),
                    @Parameter(name = "branchId", description = "지점 ID", required = true, example = "101"),
                    @Parameter(name = "month", description = "조회할 월", required = true, example = "5")
            }
    )
    @GetMapping("/{branchId}/{month}")
    public ResponseEntity<?> getMonthSalary(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long branchId,
            @PathVariable int month
    ) {
        List<WeeklySalaryDto> salary = salaryService.getSalaryInfo(branchId, month);
        return ResponseEntity.ok(salary);
    }



}
