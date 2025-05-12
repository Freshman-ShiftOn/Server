package com.epicode.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerRequestDTO {
    private Long branchId;
    private String email;
    private String name;
    private String phoneNums;
    private String roles;
    private String status;
    private BigDecimal cost;
}
