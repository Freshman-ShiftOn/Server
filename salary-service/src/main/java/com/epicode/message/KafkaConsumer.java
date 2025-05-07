package com.epicode.message;

import com.epicode.domain.Salary;
import com.epicode.domain.WeeklyUserSalary;
import com.epicode.dto.ScheduleWorkedEventDTO;
import com.epicode.repository.SalaryRepository;
import com.epicode.repository.WeeklyUserSalaryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class KafkaConsumer {
    private final SalaryRepository salaryRepository;
    private final WeeklyUserSalaryRepository weeklyUserSalaryRepository;

    @KafkaListener(topics = "salary-topic", groupId = "salary-group")
    public void updateWorkedSalary(String kafkaMessage) {
        log.info("Kafka Message: -> {}", kafkaMessage);
        ScheduleWorkedEventDTO dto = parseKafkaMessage(kafkaMessage);
        if (dto == null) return;

        WeeklyUserSalaryKey key = extractWeekKey(dto.getStartTime());
        int minutes = calculateWorkedMinutes(dto.getStartTime(), dto.getEndTime());
        //BigDecimal hourlyWage = getHourlyWage(dto.getUserId(), dto.getBranchId());
        Optional<BigDecimal> hourlyWageOpt = getHourlyWage(dto.getUserId(), dto.getBranchId());
        if (hourlyWageOpt.isEmpty()) {
            log.warn("시급 정보 없음 → userId={}, branchId={}", dto.getUserId(), dto.getBranchId());
            return;
        }
        BigDecimal hourlyWage = hourlyWageOpt.get();
        upsertWeeklySalary(dto.getUserId(), dto.getBranchId(), key, minutes, hourlyWage);
    }

    @KafkaListener(topics = "salary-delete-topic", groupId = "salary-group")
    public void handleScheduleDeleted(String kafkaMessage) {
        log.info("[DELETE] Kafka Message: {}", kafkaMessage);
        ScheduleWorkedEventDTO dto = parseKafkaMessage(kafkaMessage);
        if (dto == null) return;

        WeeklyUserSalaryKey key = extractWeekKey(dto.getStartTime());
        int minutes = calculateWorkedMinutes(dto.getStartTime(), dto.getEndTime());
        //BigDecimal hourlyWage = getHourlyWage(dto.getUserId(), dto.getBranchId());
        Optional<BigDecimal> hourlyWageOpt = getHourlyWage(dto.getUserId(), dto.getBranchId());
        if (hourlyWageOpt.isEmpty()) {
            log.warn("시급 정보 없음 → userId={}, branchId={}", dto.getUserId(), dto.getBranchId());
            return;
        }
        BigDecimal hourlyWage = hourlyWageOpt.get();
        subtractWeeklySalary(dto.getUserId(), dto.getBranchId(), key, minutes, hourlyWage);
    }

    // ===== 공통 함수 =====

    private ScheduleWorkedEventDTO parseKafkaMessage(String kafkaMessage) {
        try {
            return new ObjectMapper().readValue(kafkaMessage, ScheduleWorkedEventDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 파싱 오류", e);
            return null;
        }
    }

    private int calculateWorkedMinutes(Date start, Date end) {
        return (int) ChronoUnit.MINUTES.between(
                start.toInstant(), end.toInstant()
        );
    }

    private WeeklyUserSalaryKey extractWeekKey(Date date) {
        ZonedDateTime zdt = date.toInstant().atZone(ZoneId.systemDefault());
        return new WeeklyUserSalaryKey(
                zdt.get(IsoFields.WEEK_BASED_YEAR),
                zdt.getMonthValue(),
                zdt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        );
    }

    private Optional<BigDecimal> getHourlyWage(Long userId, Long branchId) {
        return salaryRepository
                .findByUserIdAndBranchId(userId, branchId)
                .map(Salary::getBasicSalary);
    }

    private void upsertWeeklySalary(Long userId, Long branchId, WeeklyUserSalaryKey key,
                                    int minutesToAdd, BigDecimal hourlyWage) {
        Optional<WeeklyUserSalary> existing = weeklyUserSalaryRepository
                .findByUserIdAndBranchIdAndYearAndMonthAndWeek(userId, branchId, key.year, key.month, key.week);

        if (existing.isPresent()) {
            WeeklyUserSalary salary = existing.get();
            int updatedMinutes = salary.getTotalMinutes() + minutesToAdd;

            salary.setTotalMinutes(updatedMinutes);
            salary.setCalculatedSalary(hourlyWage.multiply(BigDecimal.valueOf(updatedMinutes))
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
            salary.setWeeklyAllowanceEligible(updatedMinutes >= 900);
            salary.setUpdatedAt(LocalDateTime.now());
            weeklyUserSalaryRepository.save(salary);
            log.info("▶ 급여 계산 디버그 → userId={}, branchId={}, 시급={}, 분={}, 계산결과={}",
                    userId,
                    branchId,
                    hourlyWage,
                    updatedMinutes,
                    hourlyWage.multiply(BigDecimal.valueOf(updatedMinutes))
                            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP)
            );
        } else {
            WeeklyUserSalary newSalary = WeeklyUserSalary.builder()
                    .userId(userId)
                    .branchId(branchId)
                    .year(key.year)
                    .month(key.month)
                    .week(key.week)
                    .totalMinutes(minutesToAdd)
                    .calculatedSalary(hourlyWage.multiply(BigDecimal.valueOf(minutesToAdd))
                            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP))
                    .weeklyAllowanceEligible(minutesToAdd >= 900)
                    .updatedAt(LocalDateTime.now())
                    .build();
            weeklyUserSalaryRepository.save(newSalary);
        }
    }

    private void subtractWeeklySalary(Long userId, Long branchId, WeeklyUserSalaryKey key,
                                      int minutesToSubtract, BigDecimal hourlyWage) {
        Optional<WeeklyUserSalary> existing = weeklyUserSalaryRepository
                .findByUserIdAndBranchIdAndYearAndMonthAndWeek(userId, branchId, key.year, key.month, key.week);

        if (existing.isPresent()) {
            WeeklyUserSalary salary = existing.get();
            int updatedMinutes = Math.max(0, salary.getTotalMinutes() - minutesToSubtract);

            salary.setTotalMinutes(updatedMinutes);
            salary.setCalculatedSalary(hourlyWage.multiply(BigDecimal.valueOf(updatedMinutes))
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP));
            salary.setWeeklyAllowanceEligible(updatedMinutes >= 900);
            salary.setUpdatedAt(LocalDateTime.now());
            weeklyUserSalaryRepository.save(salary);
        } else {
            log.warn("삭제 대상 주차 데이터 없음 → 무시됨");
        }
    }

    private static class WeeklyUserSalaryKey {
        final int year;
        final int month;
        final int week;

        WeeklyUserSalaryKey(int year, int month, int week) {
            this.year = year;
            this.month = month;
            this.week = week;
        }
    }
}
