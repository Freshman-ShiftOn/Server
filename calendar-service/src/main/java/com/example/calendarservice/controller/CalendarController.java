package com.example.calendarservice.controller;

import com.example.calendarservice.model.Schedule;
import com.example.calendarservice.model.ShiftRequest;
import com.example.calendarservice.service.ScheduleService;
import com.example.calendarservice.service.ShiftRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "calendar-service-controller", description = "Calendar 서비스 API")
public class CalendarController {
    private final Environment env;
    private final ScheduleService scheduleService;
    private final ShiftRequestService shiftRequestService;

    @GetMapping("/check")
    @Operation(summary = "캘린더API 동작 테스트", description = "동작 테스트를 위한 API, 동작 포트 확인")
    public String check(HttpServletRequest request)
    {
        log.info("Server port={}", request.getServerPort());
        return String.format("Hi, There This is a message from Manual Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    // 지점 스케줄 목록 조회 (월단위)
    @GetMapping("/{branchId}/{month}")
    @Operation(summary = "지점 스케줄 목록 조회 (월단위)", description = "해당 매장의 지점 스케줄을 월 단위로 조회한다.")
    public ResponseEntity<List<Schedule>> getSchedulesByBranchId(
            @PathVariable Integer branchId,
            @PathVariable Integer month) {
        List<Schedule> schedules = scheduleService.getSchedulesByBranchId(branchId, month);
        return ResponseEntity.ok(schedules);
    }

    // 지점 스케줄 유저 별 조회 (월 단위)
    @GetMapping("/{branchId}/{month}/{userId}")
    @Operation(summary = "지점 스케줄 유저 별 조회 (월 단위)", description = "해당 매장의 특정 유저 스케줄을 월 단위로 조회한다.")
    public ResponseEntity<List<Schedule>> getSchedulesByBranchIdAndUserId(
            @PathVariable Integer branchId,
            @PathVariable Integer month,
            @PathVariable Integer userId) {
        List<Schedule> schedules = scheduleService.getSchedulesByBranchIdAndUserId(branchId, month, userId);
        return ResponseEntity.ok(schedules);
    }

    // 내 스케줄 등록
    @PostMapping("/{branchId}/{userId}")
    @Operation(summary = "내 스케줄 등록", description = "현재 사용자의 스케줄을 등록한다.")
    public ResponseEntity<Schedule> registerSchedule(
            @PathVariable Integer branchId,
            @PathVariable Integer userId,
            @RequestBody Schedule schedule) {
        schedule.setBranchId(branchId);
        schedule.setWorkerId(userId);
        Schedule newSchedule = scheduleService.createSchedule(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);
    }

    // 내 스케줄 수정
    @PutMapping("/{branchId}/{userId}/{scheduleId}")
    @Operation(summary = "내 스케줄 수정", description = "현재 사용자의 특정 스케줄을 수정한다.")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Integer branchId,
            @PathVariable Integer userId,
            @PathVariable Integer scheduleId,
            @RequestBody Schedule schedule) {
        schedule.setBranchId(branchId);
        schedule.setWorkerId(userId);
        Schedule updatedSchedule = scheduleService.updateSchedule(scheduleId, schedule);
        return ResponseEntity.ok(updatedSchedule);
    }

    // 내 스케줄 삭제
    @DeleteMapping("/{branchId}/{userId}/{scheduleId}")
    @Operation(summary = "내 스케줄 삭제", description = "현재 사용자의 특정 스케줄을 삭제한다.")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Integer branchId,
            @PathVariable Integer userId,
            @PathVariable Integer scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    // 대타 요청
    @PostMapping("/{branchId}/{userId}/request-shift")
    @Operation(summary = "대타 요청", description = "현재 사용자가 대타를 요청한다.")
    public ResponseEntity<ShiftRequest> requestShift(
            @PathVariable Integer branchId,
            @PathVariable Integer userId,
            @RequestBody ShiftRequest shiftRequest) {
        shiftRequest.setBranchId(branchId);
        shiftRequest.setWorkerId(userId.toString());
        ShiftRequest newRequest = shiftRequestService.createShiftRequest(shiftRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRequest);
    }

    // 대타 수락
    @PutMapping("/{branchId}/{userId}/request-shift/{reqShiftId}")
    @Operation(summary = "대타 수락", description = "현재 사용자가 특정 대타 요청을 수락한다.")
    public ResponseEntity<ShiftRequest> acceptShiftRequest(
            @PathVariable Integer branchId,
            @PathVariable Integer userId,
            @PathVariable Integer reqShiftId) {
        ShiftRequest updatedRequest = shiftRequestService.acceptShiftRequest(reqShiftId, userId.toString());
        return ResponseEntity.ok(updatedRequest);
    }

    // 대타 삭제
    @DeleteMapping("/{branchId}/{userId}/request-shift/{reqShiftId}")
    @Operation(summary = "대타 삭제", description = "현재 사용자가 특정 대타 요청을 삭제한다.")
    public ResponseEntity<Void> deleteShiftRequest(
            @PathVariable Integer branchId,
            @PathVariable Integer userId,
            @PathVariable Integer reqShiftId) {
        shiftRequestService.deleteShiftRequest(reqShiftId);
        return ResponseEntity.noContent().build();
    }
}