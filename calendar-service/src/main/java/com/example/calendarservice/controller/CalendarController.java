package com.example.calendarservice.controller;

import com.example.calendarservice.dto.*;
import com.example.calendarservice.model.Schedule;
import com.example.calendarservice.model.ShiftRequest;
import com.example.calendarservice.service.ScheduleService;
import com.example.calendarservice.service.ShiftRequestService;
import com.example.calendarservice.service.UserService;
import com.example.calendarservice.exception.ScheduleConflictException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "calendar-service-controller", description = "Calendar 서비스 API")
public class CalendarController {
    private final ScheduleService scheduleService;
    private final ShiftRequestService shiftRequestService;
    private final UserService userService;

    // 지점 스케줄 목록 조회 (월단위)
    @GetMapping("/{branchId}/{month}")
    @Operation(summary = "지점 스케줄 목록 조회 (월단위)", description = "해당 매장의 지점 스케줄을 월 단위로 조회한다.")
    public ResponseEntity<List<Schedule>> getSchedulesByBranchId(
            @PathVariable Long branchId,
            @PathVariable Integer month) {
        List<Schedule> schedules = scheduleService.getSchedulesByBranchId(branchId, month);
        return ResponseEntity.ok(schedules);
    }

    // 지점 스케줄 유저 별 조회 (월 단위)
    @GetMapping("/{branchId}/{month}/user")
    @Operation(summary = "지점 스케줄 유저 별 조회 (월 단위)", description = "해당 매장의 특정 유저 스케줄을 월 단위로 조회한다.")
    public ResponseEntity<List<Schedule>> getSchedulesByBranchIdAndUserId(
            @PathVariable Long branchId,
            @PathVariable Integer month,
            @RequestHeader("X-Authenticated-User-Id") String userId) {
        List<Schedule> schedules = scheduleService.getSchedulesByBranchIdAndUserId(branchId, month, Long.valueOf(userId));
        return ResponseEntity.ok(schedules);
    }

    // 내 스케줄 등록
    @PostMapping("/{branchId}")
    @Operation(summary = "내 스케줄 등록", description = "현재 사용자의 스케줄을 등록한다.")
    public ResponseEntity<Schedule> registerSchedule(
            @PathVariable Long branchId,
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @RequestBody Schedule schedule) {
        try {
            schedule.setBranchId(branchId);
            schedule.setWorkerId(Long.valueOf(userId));
            schedule.setWorkerName(userService.getUserNameById(Long.valueOf(userId)));
            Schedule newSchedule = scheduleService.createSchedule(schedule);
            return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);
        } catch (ScheduleConflictException e) {
            throw e;
        }
    }

    // 반복 스케줄 등록
    @PostMapping("/{branchId}/bulk")
    @Operation(summary = "반복 스케줄 등록", description = "현재 사용자의 반복 스케줄을 등록한다.")
    public ResponseEntity<List<Schedule>> registerSchedules(
            @PathVariable Long branchId,
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @RequestBody RepeatScheduleRequest repeatScheduleRequest) {
        try {
            repeatScheduleRequest.setBranchId(branchId);
            repeatScheduleRequest.setWorkerId(Long.valueOf(userId));
            repeatScheduleRequest.setWorkerName(userService.getUserNameById(Long.valueOf(userId)));

            List<Schedule> schedules = scheduleService.createRepeatSchedules(repeatScheduleRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(schedules);
        } catch (ScheduleConflictException e) {
            throw e;
        }
    }

    // 내 스케줄 수정
    @PutMapping("/{scheduleId}")
    @Operation(summary = "내 스케줄 수정", description = "현재 사용자의 특정 스케줄을 수정한다.")
    public ResponseEntity<Schedule> updateSchedule(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long scheduleId,
            @RequestBody Schedule schedule) {
        if (!scheduleService.isUserSchedule(scheduleId, Long.valueOf(userId))) {
            throw new IllegalArgumentException("Unauthorized: The schedule does not belong to the authenticated user.");
        }

        try {
            schedule.setWorkerId(Long.valueOf(userId));
            Schedule updatedSchedule = scheduleService.updateSchedule(scheduleId, schedule);
            return ResponseEntity.ok(updatedSchedule);
        } catch (ScheduleConflictException e) {
            throw e;
        }
    }

    // 반복 스케줄 수정
    @PutMapping("/{scheduleId}/bulk")
    @Operation(summary = "반복 스케줄 수정", description = "현재 사용자의 반복 스케줄을 수정한다.")
    public ResponseEntity<List<Schedule>> updateSchedules(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long scheduleId,
            @RequestBody RepeatScheduleUpdateRequest request) {

        // 해당 스케줄이 현재 유저의 스케줄인지 검증
        if (!scheduleService.isUserSchedule(scheduleId, Long.valueOf(userId))) {
            throw new IllegalArgumentException("Unauthorized: The schedule does not belong to the authenticated user.");
        }

        List<Schedule> updatedSchedules = scheduleService.updateRepeatSchedule(scheduleId, request);
        return ResponseEntity.ok(updatedSchedules);
    }

    // 내 스케줄 삭제
    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "스케줄 삭제", description = "특정 스케줄을 삭제한다.")
    public ResponseEntity<Void> deleteSchedule(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long scheduleId) throws IllegalAccessException {

        // 해당 스케줄이 현재 유저의 스케줄인지 검증
        if (!scheduleService.isUserSchedule(scheduleId, Long.valueOf(userId))) {
            throw new IllegalAccessException("Unauthorized: The schedule does not belong to the authenticated user.");
        }

        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    // 반복 스케줄 삭제
    @DeleteMapping("/{scheduleId}/bulk")
    @Operation(summary = "반복 스케줄 삭제", description = "반복 스케줄을 삭제한다.")
    public ResponseEntity<Void> deleteSchedules(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long scheduleId,
            @RequestParam String deleteOption) throws IllegalAccessException {

        // 해당 스케줄이 현재 유저의 스케줄인지 검증
        if (!scheduleService.isUserSchedule(scheduleId, Long.valueOf(userId))) {
            throw new IllegalAccessException("Unauthorized: The schedule does not belong to the authenticated user.");
        }

        scheduleService.deleteRepeatSchedule(scheduleId, deleteOption);
        return ResponseEntity.noContent().build();
    }

    // 대타 요청
    @PostMapping("/{branchId}/request-shift")
    @Operation(summary = "대타 요청", description = "대타를 요청한다.")
    public ResponseEntity<ShiftRequest> requestShift(
            @PathVariable Long branchId,
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @RequestBody ShiftRequest shiftRequest) {

        shiftRequest.setBranchId(branchId);
        shiftRequest.setWorkerId(Long.valueOf(userId));
        shiftRequest.setWorkerName(userService.getUserNameById(Long.valueOf(userId)));
        ShiftRequest newRequest = shiftRequestService.createShiftRequest(shiftRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRequest);
    }

    // 대타 수정
    @PutMapping("/request-shift/{reqShiftId}")
    @Operation(summary = "대타 수정", description = "대타 요청을 수정한다.")
    public ResponseEntity<ShiftRequest> updateShiftRequest(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long reqShiftId,
            @RequestBody UpdateShiftRequestDto updateShiftRequestDto) {
        ShiftRequest updatedRequest = shiftRequestService.updateShiftRequest(reqShiftId, updateShiftRequestDto.getReason());
        return ResponseEntity.ok(updatedRequest);
    }

    // 대타 삭제
    @DeleteMapping("/request-shift/{reqShiftId}")
    @Operation(summary = "대타 삭제", description = "특정 대타 요청을 삭제한다.")
    public ResponseEntity<Void> deleteShiftRequest(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long reqShiftId) throws IllegalAccessException {

        // 검증: 요청한 유저가 해당 대타 요청의 작성자인지 확인
        if (!shiftRequestService.isUserShiftRequest(reqShiftId, Long.valueOf(userId))) {
            throw new IllegalAccessException("Unauthorized: This shift request does not belong to the authenticated user.");
        }
        shiftRequestService.deleteShiftRequest(reqShiftId);
        return ResponseEntity.noContent().build();
    }

    // 대타 수락
    @PutMapping("/request-shift/{reqShiftId}/accept")
    @Operation(summary = "대타 수락", description = "현재 사용자가 특정 대타 요청을 수락한다.")
    public ResponseEntity<ShiftRequest> acceptShiftRequest(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable Long reqShiftId) {
        String userName = userService.getUserNameById(Long.valueOf(userId));
        ShiftRequest updatedRequest = shiftRequestService.acceptShiftRequest(reqShiftId, Long.valueOf(userId), userName);
        return ResponseEntity.ok(updatedRequest);
    }

    // 대타 요청 내역 조회 (지점 필터 추가)
    @GetMapping("/request-shift")
    @Operation(summary = "대타 요청 내역 조회 (마이페이지용)", description = "특정한 유저의 대타 요청 내역을 조회한다.")
    public ResponseEntity<List<ShiftRequestDto>> getShiftRequestsByUser(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @RequestParam(value = "branchId", required = false) Long branchId) {

        List<ShiftRequestDto> dtos = shiftRequestService.getShiftRequestsByUser(Long.valueOf(userId), branchId);
        return ResponseEntity.ok(dtos);
    }

    // 대타 수락 내역 조회 (지점 필터 추가)
    @GetMapping("/accepted-shift")
    @Operation(summary = "대타 수락 내역 조회 (마이페이지용)", description = "특정 유저가 수락한 대타 요청 내역을 조회한다.")
    public ResponseEntity<List<ShiftRequestDto>> getAcceptedShiftRequestsByUser(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @RequestParam(value = "branchId", required = false) Long branchId) {

        List<ShiftRequestDto> dtos = shiftRequestService.getAcceptedShiftRequestsByUser(Long.valueOf(userId), branchId);

        return ResponseEntity.ok(dtos);
    }

    // 지점 대타 요청 내역 조회 (캘린더용)
    @GetMapping("/{branchId}/{month}/request-shift")
    @Operation(summary = "지점 대타 요청 내역 조회 (캘린더용)",
            description = "특정 지점과 월에 해당하는 대타 요청 내역을 조회한다. " +
                    "결과에서 현재 사용자의 요청(myRequests)과 다른 사용자의 요청(othersRequests)을 구분해 반환한다.")
    public ResponseEntity<ShiftRequestsByBranchMonthResponseDto> getShiftRequestsByBranchAndMonth(
            @PathVariable Long branchId,
            @PathVariable Integer month,
            @RequestHeader("X-Authenticated-User-Id") String userId) {

        List<ShiftRequest> allRequests = shiftRequestService.getShiftRequestsByBranchAndMonth(branchId, month);

        // 현재 사용자의 요청과 다른 사용자의 요청을 분리
        List<ShiftRequest> myRequests = new ArrayList<>();
        List<ShiftRequest> othersRequests = new ArrayList<>();

        Long currentUserId = Long.valueOf(userId);

        for (ShiftRequest request : allRequests) {
            if (request.getWorkerId().equals(currentUserId)) {
                myRequests.add(request);
            } else {
                othersRequests.add(request);
            }
        }

        ShiftRequestsByBranchMonthResponseDto response =
                new ShiftRequestsByBranchMonthResponseDto(myRequests, othersRequests);

        return ResponseEntity.ok(response);
    }
}
