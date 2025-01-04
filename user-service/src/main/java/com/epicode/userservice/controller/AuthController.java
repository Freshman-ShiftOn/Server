package com.epicode.userservice.controller;
import com.epicode.userservice.service.KakaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final Environment env;
    private final KakaoService kakaoService;

    public AuthController(Environment env,KakaoService kakaoService) {
        this.env = env;
        this.kakaoService = kakaoService;
    }

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
}