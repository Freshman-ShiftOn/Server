package com.epicode.userservice.controller;
import com.epicode.userservice.domain.User;
import com.epicode.userservice.repository.UserRepository;
import com.epicode.userservice.security.JwtUtil;
import com.epicode.userservice.service.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "auth-service-controller", description = "OAuth2.0 Auth 서비스 API")
public class AuthController {
    private final Environment env;
    private final KakaoService kakaoService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/kakao/login")
    public String redirectToKakao() {
        try {
            String clientId = env.getProperty("spring.security.oauth2.client.registration.kakao.client-id");
            String redirectUri = env.getProperty("spring.security.oauth2.client.registration.kakao.redirect-uri");
            if (clientId == null || redirectUri == null) {
                throw new IllegalStateException("url error! yml 파일 확인 요망");
            }

            String kakaoAuthUrl = String.format(
                    "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                    clientId, redirectUri);
            return "redirect:" + kakaoAuthUrl;

        } catch (Exception e) {
            System.err.println("Error Kakao OAuth URL: " + e.getMessage());
            return "error/redirect-error";
        }
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<String> handleKakaoCallback(@RequestParam String code) {
        log.info("Request Parameters: {}", code);

        try {
            String jwtToken = kakaoService.authenticateWithKakao(code);
            return ResponseEntity.ok(jwtToken);
        } catch (Exception e) {
            System.err.println("카카오 콜백 오류(JWT발급 실패): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("콜백url 오류: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            token = token.substring(7);
            String email = jwtUtil.getEmailFromToken(token);

            User user = userRepository.findByEmail(email);
            //애플리케이션 레벨에서도 데이터 검증
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                throw new IllegalStateException("Email must not be null or empty");
            }

            Map<String, Object> response = Map.of(
                    "X-User-Id", user.getId(),//유저 고유id 반환
                    "X-Authenticated-User", user.getEmail()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation failed: " + e.getMessage());
        }
    }

}