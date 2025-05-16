package com.epicode.controller;

import com.epicode.dto.WeeklyBranchUserSummaryDTO;
import com.epicode.dto.WeeklySalaryDetailDTO;
import com.epicode.dto.WeeklySalaryDto;
import com.epicode.service.SalaryService;
import com.epicode.service.SalarySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
@Tag(name = "급여 관리 Web API", description = "웹 사용자 기본 시급 및 특별 시급 관련 API를 제공합니다.")
public class SalaryWebController {
    private final SalaryService salaryService;
    private final SalarySummaryService summaryService;

    @Operation(
            summary = "월 총 급여 조회-토탈",
            description = "월, 지점Id을 기준으로 지점의 월 급여 총액을 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", required = true, example = "Bearer ey..."),
                    @Parameter(name = "branchId", required = true, example = "101"),
                    @Parameter(name = "startDate", required = true, example = "2025-05-01"),
                    @Parameter(name = "endDate", required = true, example = "2025-05-31")
            }
    )
    @GetMapping("{branchId}/total")
    public ResponseEntity<?> getBranchSalaryTotal(
            @PathVariable Long branchId,
            @RequestParam String startDate,
            @RequestParam String endDate

    ) {
        LocalDate start = LocalDate.parse(startDate);  // yyyy-MM-dd 형식
        LocalDate end = LocalDate.parse(endDate);
        BigDecimal month_salaries = summaryService.getTotalSalarySumByBranch(branchId,start,end);
        return ResponseEntity.ok(month_salaries);
    }

    @Operation(
            summary = "기간 급여 조회",
            description = "시작일~마감일에 해당하는 지점의 급여 정보를 조회합니다.(주휴 포함)",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰 (Bearer 형식)", required = true, example = "Bearer eyJhbGciOiJIUzI1Ni..."),
                    @Parameter(name = "branchId", required = true, example = "101"),
                    @Parameter(name = "startDate", required = true, example = "2025-05-01"),
                    @Parameter(name = "endDate", required = true, example = "2025-05-31")
            }
    )
    @GetMapping("{branchId}/summary-list")
    public ResponseEntity<?> getWeeklyBranchSummary(
            @PathVariable Long branchId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);  // yyyy-MM-dd 형식
        LocalDate end = LocalDate.parse(endDate);

        List<WeeklyBranchUserSummaryDTO> result = summaryService.getWeeklySummaryByBranch(branchId, start, end);
        if (result.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "사용자 상세 급여 조회",
            description = "userId, 기간, 지점을 기준으로 특정 사용자의 급여 상세를 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", required = true, example = "Bearer ey..."),
                    @Parameter(name = "branchId", required = true, example = "101"),
                    @Parameter(name = "userId", required = true, example = "5"),
                    @Parameter(name = "startDate", required = true, example = "2025-05-01"),
                    @Parameter(name = "endDate", required = true, example = "2025-05-31")
            }
    )
    @GetMapping("{branchId}/detail")
    public ResponseEntity<?> getUserSalaryDetail(
            @PathVariable Long branchId,
            @RequestParam Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        WeeklySalaryDetailDTO detail = summaryService.getUserWeeklySalaryDetail(branchId, userId, start, end);
        return ResponseEntity.ok(detail);
    }

    @Operation(
            summary = "월간 주별 급여 조회-상세",
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
            summary = "월간 급여 조회-상세",
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
