package com.example.manualservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualTaskDTO {
    private Integer id;
    private Integer manualId;
    private String content;
    private Integer taskNo;
    private String imageUrl;
}
