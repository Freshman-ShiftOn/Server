package com.example.calendarservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftRequestDto {
    private Long id;
    private Long workerId;
    private String workerName;
    private Long branchId;
    private String branchName; // 지점 이름 추가
    private Long scheduleId;
    private List<String> workType;
    private Date reqStartTime;
    private Date reqEndTime;
    private Long acceptId;
    private String acceptName;
    private String reqStatus;
    private String reason;
    private Date lastUpdated;
}
