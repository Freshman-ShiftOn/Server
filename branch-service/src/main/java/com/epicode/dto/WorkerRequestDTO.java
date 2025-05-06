package com.epicode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long cost;
}
