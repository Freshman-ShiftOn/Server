package com.example.manualservice.controller;

import com.example.manualservice.dto.ManualDTO;
import com.example.manualservice.service.ManualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/manuals/{branchId}")
@RequiredArgsConstructor
public class ManualController {
    private final ManualService manualService;

    // 매뉴얼 목록 조회
    @GetMapping
    public ResponseEntity<List<ManualDTO>> getManualsByBranchId(@PathVariable Integer branchId) {
        List<ManualDTO> manuals = manualService.getManualsByBranchId(branchId);
        return ResponseEntity.ok(manuals);
    }

    // 매뉴얼 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ManualDTO> getManualByIdAndBranchId(@PathVariable Integer branchId, @PathVariable Integer id) {
        ManualDTO manual = manualService.getManualByIdAndBranchId(id, branchId);
        return ResponseEntity.ok(manual);
    }
}