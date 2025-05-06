package com.epicode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerDTO {
    private String name;
    private String phoneNums;
    private String roles;
    private Integer cost;
}
