package com.epicode.message;

import com.epicode.domain.DailyUserSalary;
import com.epicode.domain.Salary;
import com.epicode.dto.ScheduleWorkedEventDTO;
import com.epicode.repository.DailyUserSalaryRepository;
import com.epicode.repository.SalaryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class KafkaConsumer {

    private final SalaryRepository salaryRepository;
    private final DailyUserSalaryRepository dailyUserSalaryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "salary-topic", groupId = "salary-group")
    public void updateWorkedSalary(String kafkaMessage) {
        log.info("Kafka Message: -> {}", kafkaMessage);
        ScheduleWorkedEventDTO dto = parseKafkaMessage(kafkaMessage);
        if (dto == null) return;

        Optional<BigDecimal> hourlyWageOpt = getHourlyWage(dto.getUserId(), dto.getBranchId());
        if (hourlyWageOpt.isEmpty()) {
            log.warn("시급 정보 없음 → userId={}, branchId={}", dto.getUserId(), dto.getBranchId());
            return;
        }

        BigDecimal hourlyWage = hourlyWageOpt.get();
        int minutes = calculateWorkedMinutes(dto.getStartTime(), dto.getEndTime());
        LocalDate workDate = toLocalDate(dto.getStartTime());
        LocalTime startTime = toLocalTime(dto.getStartTime());
        LocalTime endTime = toLocalTime(dto.getEndTime());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String workTimeStr = startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);

        saveOrUpdateDailySalary(dto, workDate, minutes, hourlyWage, workTimeStr);
    }

    //일급 누적
    private void saveOrUpdateDailySalary(ScheduleWorkedEventDTO dto, LocalDate workDate, int minutes, BigDecimal hourlyWage, String workTimeStr) {
        BigDecimal dailySalary = hourlyWage.multiply(BigDecimal.valueOf(minutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        Optional<DailyUserSalary> existing = dailyUserSalaryRepository
                .findByUserIdAndBranchIdAndWorkDate(
                        dto.getUserId(), dto.getBranchId(), workDate
                );

        if (existing.isPresent()) {
            DailyUserSalary daily = existing.get();
            daily.setWorkedMinutes(daily.getWorkedMinutes() + minutes);
            daily.setDailySalary(daily.getDailySalary().add(dailySalary));
            daily.setCreatedAt(LocalDateTime.now());
            dailyUserSalaryRepository.save(daily);
            log.info("일급 누적 저장 → userId={}, date={}, type={}, 누적분={}, 누적금액={}",
                    dto.getUserId(), workDate, daily.getWorkedMinutes(), daily.getDailySalary());
        } else {
            DailyUserSalary newDaily = DailyUserSalary.builder()
                    .userId(dto.getUserId())
                    .branchId(dto.getBranchId())
                    .workDate(workDate)//일
                    .workTime(workTimeStr)
                    .workedMinutes(minutes)
                    .dailySalary(dailySalary)
                    .workType(dto.getWorkType())
                    .createdAt(LocalDateTime.now())
                    .build();

            dailyUserSalaryRepository.save(newDaily);
            log.info("일급 저장 완료 → userId={}, date={}, 분={}, 금액={}",
                    dto.getUserId(), workDate, minutes, dailySalary);
        }
    }

    private ScheduleWorkedEventDTO parseKafkaMessage(String message) {
        try {
            return objectMapper.readValue(message, ScheduleWorkedEventDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 파싱 실패", e);
            return null;
        }
    }

    private int calculateWorkedMinutes(Date start, Date end) {
        return (int) ChronoUnit.MINUTES.between(start.toInstant(), end.toInstant());
    }

    private Optional<BigDecimal> getHourlyWage(Long userId, Long branchId) {
        return salaryRepository.findByUserIdAndBranchId(userId, branchId).map(Salary::getBasicSalary);
    }

    //날짜(yyyy-MM-dd)
    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    //시간(TT:mm)
    private LocalTime toLocalTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalTime();
    }


}

