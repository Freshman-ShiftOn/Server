package com.epicode.manualservice.dto;

import com.epicode.manualservice.model.Manual;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ManualDTO {
    private Integer id;
    private Integer branchId;
    private String title;
    private Integer workerId;
    private Date lastUpdated;
    private List<ManualTaskDTO> tasks;
    private boolean isFavorite;

    // DTO 변환 메서드
    public ManualDTO toManualDTO(Manual manual) {
        return ManualDTO.builder()
                .id(manual.getId())
                .branchId(manual.getBranchId())
                .title(manual.getTitle())
                .workerId(manual.getWorkerId())
                .lastUpdated(manual.getLastUpdated())
                .tasks(manual.getTasks() != null ?
                        manual.getTasks().stream()
                                .map(task -> new ManualTaskDTO().toTaskDTO(task)) // 인스턴스를 생성해서 호출, ManualTask → ManualTaskDTO 변환
                                .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    // ManualDTO → Manual 변환
    public Manual toManualEntity() {
        Manual manual = Manual.builder()
                .id(this.id)
                .branchId(this.branchId)
                .title(this.title)
                .workerId(this.workerId)
                .lastUpdated(this.lastUpdated)
                .build();

        if (this.tasks != null) {
            manual.setTasks(this.tasks.stream()
                    .map(taskDTO -> taskDTO.toTaskEntity(taskDTO, manual)) // ManualTaskDTO → ManualTask 변환
                    .collect(Collectors.toList()));
        }

        return manual;
    }
}