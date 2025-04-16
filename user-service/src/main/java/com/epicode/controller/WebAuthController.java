package com.epicode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import com.epicode.domain.User;
import com.epicode.dto.SignUpRequestDTO;
import com.epicode.exception.CustomException;
import com.epicode.security.JwtUtil;
import com.epicode.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/web/auth")
@Tag(name = "web-auth-service-controller", description = "OAuth2.0 Auth 서비스 API")
public class WebAuthController {
    private final Environment env;
    private final KakaoService kakaoService;
    private final UserCredentialService userCredentialService;
    private final UserService userService;
    @Operation(
            summary = "사용자 웹 카카오",
            description = "웹 로그인 카카오.",
            parameters = {
                    @Parameter(name = "email", description = "사용자 이메일", required = true)
            }
    )
   @GetMapping("/kakao/login")
   public ResponseEntity<String> KakaoWebLogin(@RequestParam String code) {
       log.info("Received Kakao authorization code: {}", code);

       try {
           // KakaoService를 통해 JWT 토큰 발급
           String jwtToken = kakaoService.BossAuthenticateWithKakao(code);
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

   @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequestDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        try {
            userCredentialService.registerUser(request);
            return ResponseEntity.ok("회원가입 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("회원가입 오류", e);
            return ResponseEntity.internalServerError().body("서버 오류");
        }
    }
    
}
