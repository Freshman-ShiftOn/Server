package com.example.manualservice.service;

import com.example.manualservice.dto.ManualDTO;
import com.example.manualservice.exception.ManualNotFoundException;
import com.example.manualservice.model.Manual;
import com.example.manualservice.repository.ManualRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
@RequiredArgsConstructor
public class ManualService {

    private final ManualRepository manualRepository;

    // 매뉴얼 목록 조회
    @Transactional(readOnly = true)
    public List<ManualDTO> getManualsByBranchId(Integer branchId) {
        return manualRepository.findByBranchId(branchId).stream()
                .map(this::toManualDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ManualDTO getManualByIdAndBranchId(Integer id, Integer branchId) {
        Manual manualById = manualRepository.findById(id)
                .orElseThrow(() -> new ManualNotFoundException("해당 ID(" + id + ")에 해당하는 메뉴얼이 존재하지 않습니다."));
        if (!manualById.getBranchId().equals(branchId)) {
            throw new ManualNotFoundException("ID(" + id + ")에 해당하는 메뉴얼이 branchId(" + branchId + ")와 일치하지 않습니다.");
        }

        return toManualDTO(manualById);
    }

    // DTO 변환 메서드
    private ManualDTO toManualDTO(Manual manual) {
        return ManualDTO.builder()
                .id(manual.getId())
                .branchId(manual.getBranchId())
                .title(manual.getTitle())
                .workerId(manual.getWorkerId())
                .lastUpdated(manual.getLastUpdated())
                .build();
    }

    private Manual toManualEntity(ManualDTO manualDTO) {
        return Manual.builder()
                .id(manualDTO.getId())
                .branchId(manualDTO.getBranchId())
                .title(manualDTO.getTitle())
                .workerId(manualDTO.getWorkerId())
                .lastUpdated(manualDTO.getLastUpdated())
                .build();
    }
}