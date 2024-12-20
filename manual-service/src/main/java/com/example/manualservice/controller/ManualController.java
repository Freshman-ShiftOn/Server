package com.example.manualservice.controller;

import com.example.manualservice.dto.ManualDTO;
import com.example.manualservice.dto.ManualTaskDTO;
import com.example.manualservice.service.ManualService;
import com.example.manualservice.service.ManualTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/manuals/{branchId}")
@RequiredArgsConstructor
public class ManualController {
    private final ManualService manualService;
    private final ManualTaskService manualTaskService;

    // 매뉴얼 목록 조회
    @GetMapping
    public ResponseEntity<List<ManualDTO>> getManualsByBranchId(@PathVariable Integer branchId) {
        List<ManualDTO> manuals = manualService.getManualsByBranchId(branchId);
        return ResponseEntity.ok(manuals);
    }

    // 매뉴얼 상세 조회 및 태스크 포함
    @GetMapping("/{id}")
    public ResponseEntity<ManualDTO> getManualWithTasks(@PathVariable Integer branchId, @PathVariable Integer id) {
        ManualDTO manual = manualService.getManualByIdAndBranchId(id, branchId);
        List<ManualTaskDTO> tasks = manualTaskService.getManualTasksByManualId(id);
        manual.setTasks(tasks);
        return ResponseEntity.ok(manual);
    }

    // 매뉴얼 생성
    @PostMapping
    public ResponseEntity<ManualDTO> createManual(@RequestBody ManualDTO manualDTO) {
        ManualDTO createdManual = manualService.createManual(manualDTO);
        return ResponseEntity.ok(createdManual);
    }

    // 매뉴얼 수정
    @PutMapping("/{id}")
    public ResponseEntity<ManualDTO> updateManual(@PathVariable Integer id, @RequestBody ManualDTO manualDTO) {
        ManualDTO updatedManual = manualService.updateManual(id, manualDTO);
        return ResponseEntity.ok(updatedManual);
    }

    // 매뉴얼 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManual(@PathVariable Integer id) {
        manualService.deleteManual(id);
        return ResponseEntity.noContent().build();
    }


    // 매뉴얼 태스크 목록 조회
    @GetMapping("/{manualId}/tasks")
    public ResponseEntity<List<ManualTaskDTO>> getManualTasksByManualId(@PathVariable Integer manualId) {
        List<ManualTaskDTO> tasks = manualTaskService.getManualTasksByManualId(manualId);
        return ResponseEntity.ok(tasks);
    }

    // 매뉴얼 태스크 생성
    @PostMapping("/{manualId}/tasks")//taskNo는 바로 직전 taskNo 가져와서 +1해주도록 프론트 로직짜야 함
    public ResponseEntity<ManualTaskDTO> createManualTask(@PathVariable Integer manualId, @RequestBody ManualTaskDTO manualTaskDTO) {
        ManualTaskDTO createdTask = manualTaskService.createManualTask(manualId, manualTaskDTO);
        return ResponseEntity.ok(createdTask);
    }

    // 매뉴얼 태스크 수정
    @PutMapping("/{manualId}/tasks/{taskId}")
    public ResponseEntity<ManualTaskDTO> updateManualTask(
            @PathVariable Integer manualId,
            @PathVariable Integer taskId,
            @RequestBody ManualTaskDTO manualTaskDTO) {
        ManualTaskDTO updatedTask = manualTaskService.updateManualTask(manualId, taskId, manualTaskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    // 매뉴얼 태스크 삭제
    @DeleteMapping("/{manualId}/tasks/{taskId}")
    public ResponseEntity<Void> deleteManualTask(@PathVariable Integer manualId, @PathVariable Integer taskId) {
        manualTaskService.deleteManualTask(manualId, taskId);
        return ResponseEntity.noContent().build();
    }

}