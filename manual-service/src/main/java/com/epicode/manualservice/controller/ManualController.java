package com.epicode.manualservice.controller;

import com.epicode.manualservice.dto.ManualDTO;
import com.epicode.manualservice.dto.ManualTaskDTO;
import com.epicode.manualservice.service.ManualService;
import com.epicode.manualservice.service.ManualTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/manuals")
@RequiredArgsConstructor
@Tag(name = "manual-service-controller", description = "Manual 서비스 API")
public class ManualController {
    private final Environment env;
    private final ManualService manualService;
    private final ManualTaskService manualTaskService;

    // 선택한 매장의 매뉴얼 목록 조회
    @GetMapping("/{branchId}")
    @Operation(summary = "매뉴얼 목록 조회", description = "해당 매장의 매뉴얼 목록 전체를 조회한다.")
    public ResponseEntity<List<ManualDTO>> getManualsByBranchId(@RequestHeader("X-Authenticated-User") String email,@RequestHeader("X-Branch-Ids") Long[] branches,@PathVariable Integer branchId) {
        List<ManualDTO> manuals = manualService.getManualsByBranchId(branchId);
        return ResponseEntity.ok(manuals);
    }

    // 매뉴얼 상세 조회 및 태스크 포함
    @GetMapping("/{branchId}/{id}")
    @Operation(summary = "매뉴얼 상세 조회 및 태스크 포함", description = "해당 매장의 매뉴얼 개별 내역(tasks)을 조회한다.")
    public ResponseEntity<ManualDTO> getManualWithTasks(@PathVariable Integer branchId, @PathVariable Integer id) {
        ManualDTO manual = manualService.getManualByIdAndBranchId(id, branchId);
        List<ManualTaskDTO> tasks = manualTaskService.getManualTasksByManualId(id);
        manual.setTasks(tasks);
        return ResponseEntity.ok(manual);
    }

    // 매뉴얼 생성
    @PostMapping("/{branchId}")
    @Operation(summary = "매뉴얼 생성", description = "해당 매장의 매뉴얼을 생성한다.")
    public ResponseEntity<ManualDTO> createManual(@RequestBody ManualDTO manualDTO) {
        ManualDTO createdManual = manualService.createManual(manualDTO);
        return ResponseEntity.ok(createdManual);
    }

    // 매뉴얼 수정
    @PutMapping("/{branchId}/{id}")
    @Operation(summary = "매뉴얼 수정", description = "해당 매장의 특정 매뉴얼을 수정한다.")
    public ResponseEntity<ManualDTO> updateManual(@PathVariable Integer id, @RequestBody ManualDTO manualDTO) {
        ManualDTO updatedManual = manualService.updateManual(id, manualDTO);
        return ResponseEntity.ok(updatedManual);
    }

    // 매뉴얼 삭제
    @DeleteMapping("/{branchId}/{id}")
    @Operation(summary = "매뉴얼 삭제", description = "해당 매장의 특정 매뉴얼을 삭제한다.")
    public ResponseEntity<Void> deleteManual(@PathVariable Integer id) {
        manualService.deleteManual(id);
        return ResponseEntity.noContent().build();
    }


    // 매뉴얼 태스크 목록 조회
    @GetMapping("/{branchId}/{manualId}/tasks")
    @Operation(summary = "매뉴얼 태스크 목록 조회", description = "해당 매장의 특정 매뉴얼의 태스크를 조회한다.")
    public ResponseEntity<List<ManualTaskDTO>> getManualTasksByManualId(@PathVariable Integer manualId) {
        List<ManualTaskDTO> tasks = manualTaskService.getManualTasksByManualId(manualId);
        return ResponseEntity.ok(tasks);
    }

    // 매뉴얼 태스크 생성
    @PostMapping("/{branchId}/{manualId}/tasks")//taskNo는 바로 직전 taskNo 가져와서 +1해주도록 프론트 로직짜야 함
    @Operation(summary = "매뉴얼 태스크 생성", description = "해당 매장의 특정 매뉴얼의 태스크를 생성. taskNo는 바로 직전 taskNo 가져와서 +1해주도록 프론트 로직짜야 함")
    public ResponseEntity<ManualTaskDTO> createManualTask(@PathVariable Integer manualId, @RequestBody ManualTaskDTO manualTaskDTO) {
        ManualTaskDTO createdTask = manualTaskService.createManualTask(manualId, manualTaskDTO);
        return ResponseEntity.ok(createdTask);
    }

    // 매뉴얼 태스크 수정
    @PutMapping("/{branchId}/{manualId}/tasks/{taskId}")
    @Operation(summary = "매뉴얼 태스크 수정", description = "해당 매장의 특정 매뉴얼의 태스크를 수정한다.")
    public ResponseEntity<ManualTaskDTO> updateManualTask(
            @PathVariable Integer manualId,
            @PathVariable Integer taskId,
            @RequestBody ManualTaskDTO manualTaskDTO) {
        ManualTaskDTO updatedTask = manualTaskService.updateManualTask(manualId, taskId, manualTaskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    // 매뉴얼 태스크 삭제
    @DeleteMapping("/{branchId}/{manualId}/tasks/{taskId}")
    @Operation(summary = "매뉴얼 태스크 삭제", description = "해당 매장의 특정 매뉴얼의 태스크를 삭제한다.")
    public ResponseEntity<Void> deleteManualTask(@PathVariable Integer manualId, @PathVariable Integer taskId) {
        manualTaskService.deleteManualTask(manualId, taskId);
        return ResponseEntity.noContent().build();
    }


}