package com.example.calendarservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Date reqStartTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Date reqEndTime;
    
    private Long acceptId;
    private String acceptName;
    private String reqStatus;
    private String reason;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Seoul")
    private Date lastUpdated;
}
