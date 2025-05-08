package com.example.calendarservice.message;

import com.example.calendarservice.dto.ScheduleWorkedEventDTO;
import com.example.calendarservice.model.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@Slf4j
public class ScheduleEventProducer {
    private final KafkaTemplate<String, ScheduleWorkedEventDTO> kafkaTemplate;

    public void sendScheduleWorkedEvent(Schedule schedule) {
        ScheduleWorkedEventDTO dto = toDto(schedule);
        CompletableFuture<SendResult<String, ScheduleWorkedEventDTO>> future =
                kafkaTemplate.send("salary-topic", dto);
        future.thenAccept(result -> {
            log.info("[Kafka 발행 성공] salary-topic → {}", dto);
        }).exceptionally(ex -> {
            log.error("[Kafka 발행 실패]", ex);
            return null;
        });
    }
    //bulk 이벤트
    public void sendSchedules(List<Schedule> schedules) {
        schedules.forEach(this::sendScheduleWorkedEvent);
    }

    public void sendScheduleDeletedEvent(Schedule schedule) {
        ScheduleWorkedEventDTO dto = toDto(schedule);

        kafkaTemplate.send("salary-delete-topic", dto); // 다른 토픽 사용 권장
    }

    //bulk 삭제
    public void sendDeletedSchedules(List<Schedule> schedules) {
        schedules.forEach(this::sendScheduleDeletedEvent);
    }

    //DTO생성
    private ScheduleWorkedEventDTO toDto(Schedule schedule) {
        return ScheduleWorkedEventDTO.builder()
                .userId(schedule.getWorkerId())
                .branchId(schedule.getBranchId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build();
    }
}
