package com.epicode.manualservice.model;
import com.epicode.manualservice.dto.ManualTaskDTO;

public class ManualTaskMapper {
    // ManualTask -> ManualTaskDTO
    public static ManualTaskDTO toDTO(ManualTask manualTask) {
        return ManualTaskDTO.builder()
                .id(manualTask.getId())
                .manualId(manualTask.getManual().getId()) // Manual 객체에서 ID 가져오기
                .content(manualTask.getContent())
                .taskNo(manualTask.getTaskNo())
                .imageUrl(manualTask.getImageUrl())
                .build();
    }

    // ManualTaskDTO -> ManualTask
    public static ManualTask toEntity(ManualTaskDTO dto, Manual manual) {
        return ManualTask.builder()
                .id(dto.getId())
                .manual(manual)
                .content(dto.getContent())
                .taskNo(dto.getTaskNo())
                .imageUrl(dto.getImageUrl())
                .build();
    }
}
