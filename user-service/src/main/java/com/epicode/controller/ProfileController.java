package com.epicode.controller;

import com.epicode.domain.User;
import com.epicode.service.KakaoService;
import com.epicode.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profile")
@Tag(name = "profile-service-controller", description = "Profile API")
public class ProfileController {
    private final UserService userService;
    @Operation(
            summary = "사용자 이름 변경",
            description = "사용자 정보를 변경합니다.",
            parameters = {
                    @Parameter(name = "name", description = "사용자 new이름", required = true)
            }
    )
    @PutMapping("/edit/{name}")
    public ResponseEntity<?> updateName(
            @RequestHeader("X-Authenticated-User-Id") String userId,
            @PathVariable String name) {
        log.debug("userId 헤더 = {}", userId);
        User updatedUser = userService.updateUserName(Long.parseLong(userId), name);
        return ResponseEntity.ok(updatedUser);
    }
}
