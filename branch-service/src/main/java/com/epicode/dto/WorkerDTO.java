package com.epicode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerDTO {
    private String name;
    private String email;
    private String phoneNums;
    private String roles;
    private String status;
    private BigDecimal cost;
}
