package com.epicode.manualservice.service;

import com.epicode.manualservice.dto.ManualDTO;
import com.epicode.manualservice.dto.ManualTaskDTO;
import com.epicode.manualservice.exception.BranchAuthorizeException;
import com.epicode.manualservice.model.Manual;
import com.epicode.manualservice.model.ManualTask;
import com.epicode.manualservice.repository.ManualRepository;
import com.epicode.manualservice.exception.ManualNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ManualService {
    private final ManualRepository manualRepository;
    private final ManualTaskService manualTaskService;

    public void validateBranchAccess(String branches, Integer branchId) {

        //System.out.println("validate branchId: " + branchId+", branches: "+branches);
        List<Integer> branchIdList = Arrays.stream(branches.split(","))
                .map(Integer::valueOf) // 문자열을 Integer로 변환
                .toList();
        //System.out.println("list branchIds: "+branchIdList.toString());
        if (!branchIdList.contains(branchId)) {
            throw new BranchAuthorizeException("해당 매장에 접근 권한이 없습니다.");
        }
    }

    // 매뉴얼 목록 조회
    @Transactional(readOnly = true)
    public List<ManualDTO> getManualsByBranchId(Integer branchId) {
        return manualRepository.findByBranchId(branchId).stream()
                .map(manual -> new ManualDTO().toManualDTO(manual)) // Manual → ManualDTO 변환
                .collect(Collectors.toList());
    }

    // 매뉴얼 상세 조회 (태스크 포함)
    @Transactional(readOnly = true)
    public ManualDTO getManualByIdAndBranchId(Integer id, Integer branchId) {
        Manual manual = manualRepository.findById(id)
                .orElseThrow(() -> new ManualNotFoundException("해당 ID(" + id + ")에 해당하는 메뉴얼이 존재하지 않습니다."));

        if (!manual.getBranchId().equals(branchId)) {
            throw new ManualNotFoundException("ID(" + id + ")에 해당하는 메뉴얼이 branchId(" + branchId + ")와 일치하지 않습니다.");
        }

        // ManualTaskDTO 리스트 생성
        List<ManualTaskDTO> tasks = manualTaskService.getManualTasksByManualId(id);

        // Manual → ManualDTO 변환 후 태스크 설정
        ManualDTO manualDTO = new ManualDTO().toManualDTO(manual);
        manualDTO.setTasks(tasks);

        return manualDTO;
    }

    // 매뉴얼 생성
    public ManualDTO createManual(ManualDTO manualDTO) {
        Manual manual = manualDTO.toManualEntity(); // ManualDTO → Manual 변환
        Manual savedManual = manualRepository.save(manual);
        return new ManualDTO().toManualDTO(savedManual); // Manual → ManualDTO 변환
    }

    // 매뉴얼 수정
    public ManualDTO updateManual(Integer manualId, ManualDTO manualDTO) {
        Manual existingManual = manualRepository.findById(manualId)
                .orElseThrow(() -> new ManualNotFoundException("해당하는 매뉴얼이 없습니다. 매뉴얼ID: " + manualId));

        // 기존 Manual 엔티티의 값 업데이트
        existingManual.setBranchId(manualDTO.getBranchId());
        existingManual.setTitle(manualDTO.getTitle());
        existingManual.setWorkerId(manualDTO.getWorkerId());

        Manual updatedManual = manualRepository.save(existingManual);
        return new ManualDTO().toManualDTO(updatedManual); // Manual → ManualDTO 변환
    }

    // 매뉴얼 삭제
    public void deleteManual(Integer manualId) {
        Manual manual = manualRepository.findById(manualId)
                .orElseThrow(() -> new ManualNotFoundException("해당하는 매뉴얼이 없습니다. 매뉴얼ID: " + manualId));
        manualRepository.delete(manual);
    }
}
