package com.epicode.controller;
import com.epicode.domain.User;
import com.epicode.repository.UserRepository;
import com.epicode.security.JwtUtil;
import com.epicode.service.KakaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
//    private final UserRepository userRepository;
//    private final JwtUtil jwtUtil;

    @GetMapping("/kakao/login")
    public ResponseEntity<String> redirectToKakao(HttpServletResponse response) {
        try {
            // 환경 변수에서 clientId와 redirectUri 가져오기
            String clientId = env.getProperty("spring.security.oauth2.client.registration.kakao.client-id");
            String redirectUri = env.getProperty("spring.security.oauth2.client.registration.kakao.redirect-uri");

            if (clientId == null || redirectUri == null) {
                throw new IllegalStateException("Kakao OAuth 환경 변수가 누락되었습니다. application.yml 파일을 확인하세요.");
            }

            // 카카오 인증 URL 생성
            String kakaoAuthUrl = String.format(
                    "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                    clientId, redirectUri);

            // 클라이언트 브라우저를 카카오 인증 페이지로 리다이렉트
            //response.sendRedirect(kakaoAuthUrl);
            return ResponseEntity.status(HttpStatus.OK).body(kakaoAuthUrl);//HttpStatus.FOUND

        } catch (Exception e) {
            log.error("카카오 리다이렉트 URL 생성 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<String> handleKakaoCallback(@RequestParam String code) {
        log.info("Received Kakao authorization code: {}", code);

        try {
            // KakaoService를 통해 JWT 토큰 발급
            String jwtToken = kakaoService.authenticateWithKakao(code);
            // JWT 토큰 반환
            return ResponseEntity.ok(jwtToken);
        } catch (IllegalStateException e) {
            log.error("카카오 인증 실패 - 상태 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("카카오 인증 실패: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("카카오 콜백 처리 중 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류: " + e.getMessage());
        }
    }

//    @GetMapping("/join")
//    public ResponseEntity<?> joinUser(@RequestHeader("X-Authenticated-User") String email,
//                                         @RequestParam String name) {
//        kakaoService.saveUser(email,name);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//    @GetMapping("/info")
//    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
//        try {
//            if (token == null || !token.startsWith("Bearer ")) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
//            }
//
//            token = token.substring(7);
//            String email = jwtUtil.getEmailFromToken(token);
//
//            User user = userRepository.findByEmail(email);
//            //애플리케이션 레벨에서도 데이터 검증
//            if (user.getEmail() == null || user.getEmail().isEmpty()) {
//                throw new IllegalStateException("Email must not be null or empty");
//            }
//
//            Map<String, Object> response = Map.of(
//                    "X-User-Id", user.getId(),//유저 고유id 반환
//                    "X-Authenticated-User", user.getEmail()
//            );
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation failed: " + e.getMessage());
//        }
//    }

}