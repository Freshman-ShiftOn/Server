package com.epicode.dto;
import lombok.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleWorkedEventDTO {
    private Long userId;
    private Long branchId;
    private Date startTime;
    private Date endTime;
}