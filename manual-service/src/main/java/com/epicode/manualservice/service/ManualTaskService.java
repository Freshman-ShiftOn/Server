package com.epicode.manualservice.service;

import com.epicode.manualservice.dto.ManualTaskDTO;
import com.epicode.manualservice.exception.ManualTaskNotFoundException;
import com.epicode.manualservice.model.Manual;
import com.epicode.manualservice.model.ManualTask;
import com.epicode.manualservice.model.ManualTaskMapper;
import com.epicode.manualservice.repository.ManualRepository;
import com.epicode.manualservice.repository.ManualTaskRepository;
import com.epicode.manualservice.exception.ManualNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ManualTaskService {

    private final ManualTaskRepository manualTaskRepository;
    private final ManualRepository manualRepository;

    // 매뉴얼 태스크 목록 조회
    public List<ManualTaskDTO> getManualTasksByManualId(Integer manualId) {
        List<ManualTask> tasks = manualTaskRepository.findByManualId(manualId);
        return tasks.stream()
                .map(ManualTaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 매뉴얼 태스크 생성
    public ManualTaskDTO createManualTask(Integer manualId, ManualTaskDTO manualTaskDTO) {
        Manual manual = manualRepository.findById(manualId)
                .orElseThrow(() -> new ManualNotFoundException("해당하는 매뉴얼이 없습니다. " + manualId));

        ManualTask manualTask = ManualTaskMapper.toEntity(manualTaskDTO, manual);
        ManualTask savedTask = manualTaskRepository.save(manualTask);
        // Manual의 lastUpdated 필드 갱신
        manual.setLastUpdated(new Date());
        manualRepository.save(manual);
        return ManualTaskMapper.toDTO(savedTask);
    }

    // 매뉴얼 태스크 수정
    public ManualTaskDTO updateManualTask(Integer manualId, Integer taskId, ManualTaskDTO manualTaskDTO) {
        ManualTask existingTask = manualTaskRepository.findById(taskId)
                .orElseThrow(() -> new ManualTaskNotFoundException("해당하는 매뉴얼이 없습니다. " + taskId));
        if (!existingTask.getManual().getId().equals(manualId)) {
            throw new IllegalArgumentException("매뉴얼 수정을 실패했습니다. - 매뉴얼 ID 오류");
        }

        existingTask.setContent(manualTaskDTO.getContent());
        existingTask.setTaskNo(manualTaskDTO.getTaskNo());
        existingTask.setImageUrl(manualTaskDTO.getImageUrl());

        ManualTask updatedTask = manualTaskRepository.save(existingTask);

        // Manual의 lastUpdated 필드 갱신
        Manual manual = existingTask.getManual();
        manual.setLastUpdated(new Date());
        manualRepository.save(manual);
        return ManualTaskMapper.toDTO(updatedTask);
    }

    // 매뉴얼 태스크 삭제
    public void deleteManualTask(Integer manualId, Integer taskId) {
        ManualTask task = manualTaskRepository.findById(taskId)
                .orElseThrow(() -> new ManualTaskNotFoundException("해당하는 매뉴얼이 없습니다. " + taskId));
        if (!task.getManual().getId().equals(manualId)) {
            throw new IllegalArgumentException("매뉴얼 삭제를 실패했습니다. - 매뉴얼 ID 오류");
        }
        manualTaskRepository.delete(task);
        // Manual의 lastUpdated 필드 갱신
        Manual manual = task.getManual();
        manual.setLastUpdated(new Date());
        manualRepository.save(manual);
    }
}
