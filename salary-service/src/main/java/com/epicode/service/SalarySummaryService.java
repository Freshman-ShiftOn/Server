package com.epicode.service;

import com.epicode.domain.DailyUserSalary;
import com.epicode.domain.UserBranch;
import com.epicode.dto.DailyWorkDetailDTO;
import com.epicode.dto.WeeklyBranchUserSummaryDTO;
import com.epicode.dto.WeeklySalaryDetailDTO;
import com.epicode.repository.DailyUserSalaryRepository;
import com.epicode.repository.UserBranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalarySummaryService {
    private final UserBranchRepository userBranchRepository;
    private final DailyUserSalaryRepository dailySalaryRepo;

    public List<WeeklyBranchUserSummaryDTO> getWeeklySummaryByBranch(Long branchId, LocalDate start, LocalDate end) {
        List<DailyUserSalary> salaries = dailySalaryRepo.findSalariesByBranchAndPeriod(branchId, start, end);
        Map<Long, List<DailyUserSalary>> groupedByUser = salaries.stream()
                .collect(Collectors.groupingBy(DailyUserSalary::getUserId));

        List<WeeklyBranchUserSummaryDTO> result = new ArrayList<>();

        for (Map.Entry<Long, List<DailyUserSalary>> entry : groupedByUser.entrySet()) {
            Long userId = entry.getKey();
            List<DailyUserSalary> userSalaries = entry.getValue();

            int totalMinutes = userSalaries.stream().mapToInt(DailyUserSalary::getWorkedMinutes).sum();
            BigDecimal totalSalary = userSalaries.stream()
                    .map(DailyUserSalary::getDailySalary)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            UserBranch user = userBranchRepository.findByUserIdAndBranchId(userId,branchId);

            boolean eligible = totalMinutes >= 900; // 예: 주 15시간 이상
            BigDecimal weeklyAllowance = eligible
                    ? user.getPersonal_cost().multiply(BigDecimal.valueOf(8))
                    : BigDecimal.ZERO;

            BigDecimal finalSalary = totalSalary.add(weeklyAllowance);

            result.add(new WeeklyBranchUserSummaryDTO(
                    userId,
                    user.getUser().getName(),
                    user.getPersonal_cost(),
                    totalMinutes,
                    totalSalary,
                    weeklyAllowance,
                    finalSalary
            ));
        }

        return result;
    }


    public WeeklySalaryDetailDTO getUserWeeklySalaryDetail(Long branchId, Long userId, LocalDate start, LocalDate end) {
        List<DailyUserSalary> salaries = dailySalaryRepo.findSalariesByBranchAndPeriod(branchId, start, end)
                .stream()
                .filter(s -> s.getUserId().equals(userId))
                .toList();

        int totalMinutes = salaries.stream().mapToInt(DailyUserSalary::getWorkedMinutes).sum();
        BigDecimal baseSalary = salaries.stream()
                .map(DailyUserSalary::getDailySalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        UserBranch user = userBranchRepository.findByUserIdAndBranchId(userId, branchId);

        boolean eligible = totalMinutes >= 900;
        BigDecimal weeklyAllowance = eligible ? user.getPersonal_cost().multiply(BigDecimal.valueOf(8)) : BigDecimal.ZERO;
        String reason = eligible ? "주 15시간 이상 근무" : "주 15시간 미만 근무";
        BigDecimal finalSalary = baseSalary.add(weeklyAllowance);


        List<DailyWorkDetailDTO> dailyDetails = salaries.stream()
                .map(s -> new DailyWorkDetailDTO(
                        s.getWorkDate(),
                        s.getWorkDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                        s.getWorkTime(),
                        s.getWorkedMinutes(),
                        s.getWorkType()
                ))
                .toList();

        return new WeeklySalaryDetailDTO(
                user.getUser().getName(),
                user.getPersonal_cost(),
                totalMinutes,
                baseSalary,
                eligible,
                reason,
                weeklyAllowance,
                finalSalary,
                dailyDetails
        );
    }
}