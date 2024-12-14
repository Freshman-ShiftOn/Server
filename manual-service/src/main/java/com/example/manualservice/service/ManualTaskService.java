package com.example.manualservice.service;

import com.example.manualservice.dto.ManualTaskDTO;
import com.example.manualservice.exception.ManualNotFoundException;
import com.example.manualservice.exception.ManualTaskNotFoundException;
import com.example.manualservice.model.Manual;
import com.example.manualservice.model.ManualTask;
import com.example.manualservice.model.ManualTaskMapper;
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
                .orElseThrow(() -> new ManualNotFoundException("Manual not found with id: " + manualId));

        ManualTask manualTask = ManualTaskMapper.toEntity(manualTaskDTO, manual);
        ManualTask savedTask = manualTaskRepository.save(manualTask);
        return ManualTaskMapper.toDTO(savedTask);
    }

    // 매뉴얼 태스크 수정
    public ManualTaskDTO updateManualTask(Integer manualId, Integer taskId, ManualTaskDTO manualTaskDTO) {
        ManualTask existingTask = manualTaskRepository.findById(taskId)
                .orElseThrow(() -> new ManualTaskNotFoundException("ManualTask not found with id: " + taskId));

        // 매뉴얼 ID 확인
        if (!existingTask.getManual().getId().equals(manualId)) {
            throw new IllegalArgumentException("Task does not belong to the specified manual");
        }

        existingTask.setContent(manualTaskDTO.getContent());
        existingTask.setTaskNo(manualTaskDTO.getTaskNo());
        existingTask.setImageUrl(manualTaskDTO.getImageUrl());

        ManualTask updatedTask = manualTaskRepository.save(existingTask);
        return ManualTaskMapper.toDTO(updatedTask);
    }

    // 매뉴얼 태스크 삭제
    public void deleteManualTask(Integer manualId, Integer taskId) {
        ManualTask task = manualTaskRepository.findById(taskId)
                .orElseThrow(() -> new ManualTaskNotFoundException("ManualTask not found with id: " + taskId));

        // 매뉴얼 ID 확인
        if (!task.getManual().getId().equals(manualId)) {
            throw new IllegalArgumentException("Task does not belong to the specified manual");
        }

        manualTaskRepository.delete(task);
    }
}
