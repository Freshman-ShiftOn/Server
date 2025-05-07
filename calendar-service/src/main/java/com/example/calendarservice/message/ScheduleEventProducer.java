package com.example.calendarservice.message;

import com.example.calendarservice.dto.ScheduleWorkedEventDTO;
import com.example.calendarservice.model.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ScheduleEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendScheduleWorkedEvent(Schedule schedule) {
        ScheduleWorkedEventDTO dto = ScheduleWorkedEventDTO.builder()
                .userId(schedule.getWorkerId())
                .branchId(schedule.getBranchId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build();

        kafkaTemplate.send("salary-topic", dto);
    }
}
