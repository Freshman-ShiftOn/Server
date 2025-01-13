package com.epicode.manualservice.dto;

import com.epicode.manualservice.model.Manual;
import com.epicode.manualservice.model.ManualTask;
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

    // Task 변환 메서드
    ManualTaskDTO toTaskDTO(ManualTask task) {
        return ManualTaskDTO.builder()
                .id(task.getId())
                .manualId(task.getManual() != null ? task.getManual().getId() : null)
                .content(task.getContent())
                .taskNo(task.getTaskNo())
                .imageUrl(task.getImageUrl())
                .build();
    }
    // ManualTaskDTO → ManualTask 변환
    public ManualTask toTaskEntity(ManualTaskDTO taskDTO, Manual manual) {
        if (manual == null) {
            throw new IllegalArgumentException("Manual cannot be null");
        }
        ManualTask manualTask = new ManualTask();
        manualTask.setId(taskDTO.getId());
        manualTask.setContent(taskDTO.getContent());
        manualTask.setTaskNo(taskDTO.getTaskNo());
        manualTask.setImageUrl(taskDTO.getImageUrl());
        manualTask.setManual(manual);
        return manualTask;
    }
}
