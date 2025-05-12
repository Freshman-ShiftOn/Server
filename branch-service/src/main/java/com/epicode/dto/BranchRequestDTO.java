package com.epicode.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchRequestDTO {
    private String name;
    private String adress;
    private String dial_numbers;
    private String basic_cost;
    private Boolean weekly_allowance;

    private Long userId;//boss id
    private String email;//boss email => for 교차검증
}
