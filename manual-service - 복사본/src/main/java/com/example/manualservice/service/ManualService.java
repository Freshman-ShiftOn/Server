package com.example.manualservice.service;

import com.example.manualservice.dto.ManualDTO;
import com.example.manualservice.exception.ManualNotFoundException;
import com.example.manualservice.exception.ManualTaskNotFoundException;
import com.example.manualservice.model.Manual;
import com.example.manualservice.model.ManualTask;
import com.example.manualservice.repository.ManualRepository;
import com.example.manualservice.repository.ManualTaskRepository;
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

    // 매뉴얼 상세 조회
    @Transactional(readOnly = true)
    public ManualDTO getManualByIdAndBranchId(Integer id, Integer branchId) {
        Manual manual = manualRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ManualNotFoundException("Manual not found with id: " + id + " and branchId: " + branchId));
        return toManualDTO(manual);
    }

    // 매뉴얼 생성
    public ManualDTO createManual(ManualDTO manualDTO) {
        Manual manual = toManualEntity(manualDTO);
        Manual savedManual = manualRepository.save(manual);
        return toManualDTO(savedManual);
    }

    // 매뉴얼 수정
    public ManualDTO updateManual(Integer manualId, ManualDTO manualDTO) {
        Manual existingManual = manualRepository.findById(manualId)
                .orElseThrow(() -> new ManualNotFoundException("Manual not found with id: " + manualId));

        existingManual.setBranchId(manualDTO.getBranchId());
        existingManual.setTitle(manualDTO.getTitle());
        existingManual.setWorkerId(manualDTO.getWorkerId());

        Manual updatedManual = manualRepository.save(existingManual);
        return toManualDTO(updatedManual);
    }

    // 매뉴얼 삭제
    public void deleteManual(Integer manualId) {
        Manual manual = manualRepository.findById(manualId)
                .orElseThrow(() -> new ManualNotFoundException("Manual not found with id: " + manualId));
        manualRepository.delete(manual);
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
