package com.epicode.manualservice.dto;

import com.epicode.manualservice.model.Manual;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualDTO {
    private Integer id;
    private Integer branchId;
    private String title;
    private Integer workerId;
    private Date lastUpdated;
    private List<ManualTaskDTO> tasks;

    public static ManualDTO fromEntity(Manual manual) {
        return ManualDTO.builder()
                .id(manual.getId())
                .branchId(manual.getBranchId())
                .title(manual.getTitle())
                .workerId(manual.getWorkerId())
                .lastUpdated(manual.getLastUpdated())
                .build();
    }

    public static Manual toEntity(ManualDTO dto) {
        Manual manual = new Manual();
        manual.setId(dto.getId());
        manual.setBranchId(dto.getBranchId());
        manual.setTitle(dto.getTitle());
        manual.setWorkerId(dto.getWorkerId());
        manual.setLastUpdated(dto.getLastUpdated());
        return manual;
    }
}