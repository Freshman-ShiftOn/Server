package com.epicode.message;

import com.epicode.domain.Salary;
import com.epicode.domain.WeeklyUserSalary;
import com.epicode.dto.ScheduleWorkedEventDTO;
import com.epicode.repository.SalaryRepository;
import com.epicode.repository.WeeklyUserSalaryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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

        ObjectMapper mapper = new ObjectMapper();
        ScheduleWorkedEventDTO dto;
        try {
            dto = mapper.readValue(kafkaMessage, ScheduleWorkedEventDTO.class);
        } catch (JsonProcessingException ex) {
            log.error("Kafka 메시지 파싱 오류", ex);
            return;
        }

        Long userId = dto.getUserId();
        Long branchId = dto.getBranchId();
        Date start = dto.getStartTime();
        Date end = dto.getEndTime();

        ZonedDateTime startZdt = start.toInstant().atZone(ZoneId.systemDefault());
        int year = startZdt.get(IsoFields.WEEK_BASED_YEAR);
        int week = startZdt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int month = startZdt.getMonthValue();

        long newMinutes = ChronoUnit.MINUTES.between(
                start.toInstant(), end.toInstant()
        );

        BigDecimal hourlyWage = salaryRepository
                .findByUserIdAndBranchId(userId, branchId)
                .map(Salary::getBasicSalary)
                .orElse(BigDecimal.ZERO);

        BigDecimal calculated = hourlyWage
                .multiply(BigDecimal.valueOf(newMinutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        Optional<WeeklyUserSalary> existing = weeklyUserSalaryRepository
                .findByUserIdAndBranchIdAndYearAndMonthAndWeek(userId, branchId, year, month, week);

        if (existing.isPresent()) { //이미 있다면, 주차 단위로 더해 업데이트
            WeeklyUserSalary salary = existing.get();
            int updatedMinutes = salary.getTotalMinutes() + (int) newMinutes;

            salary.setTotalMinutes(updatedMinutes);
            salary.setCalculatedSalary(
                    hourlyWage.multiply(BigDecimal.valueOf(updatedMinutes))
                            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP)
            );
            salary.setWeeklyAllowanceEligible(updatedMinutes >= 900);
            salary.setUpdatedAt(LocalDateTime.now());
            weeklyUserSalaryRepository.save(salary);
        } else {//저장된 정보가 없다면 바로 저장
            WeeklyUserSalary newSalary = WeeklyUserSalary.builder()
                    .userId(userId)
                    .branchId(branchId)
                    .year(year)
                    .month(month)
                    .week(week)
                    .totalMinutes((int) newMinutes)
                    .calculatedSalary(calculated)
                    .weeklyAllowanceEligible(newMinutes >= 900)
                    .updatedAt(LocalDateTime.now())
                    .build();
            weeklyUserSalaryRepository.save(newSalary);
        }
    }
}
