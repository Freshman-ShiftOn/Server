package com.epicode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.*;
import com.epicode.domain.*;
import com.epicode.dto.LoginRequestDTO;
import com.epicode.dto.SignUpRequestDTO;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.repository.*;
import com.epicode.security.JwtUtil;
import com.epicode.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/web/auth")
@Tag(name = "web-auth-service-controller", description = "Auth 서비스 API")
public class WebAuthController {
    private final JwtUtil jwtUtil;
    private final KakaoService kakaoService;
    private final UserCredentialService userCredentialService;
    private final UserCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail())!=null) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }//이미 있는 사용자-중복 가입 불가
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO request) {
        try {
            User user = userRepository.findByEmail(request.getEmail());
            if (userRepository.findByEmail(request.getEmail())!=null) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }//사용자를 찾을 수 없음
            UserCredentials credential = credentialRepository.findById(user.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_TYPE_ERROR));

            if (!passwordEncoder.matches(request.getPassword(), credential.getPasswordHash())) {
                throw new CustomException(ErrorCode.USER_PWD_INVALID);
            }
            List<Long> branchIds = Optional.ofNullable(user.getUserBranches())
                .orElse(List.of()) // null이면 빈 리스트 반환
                .stream()
                .map(ub -> ub.getBranch().getId())
                .toList();

            String token = jwtUtil.generateToken(user.getEmail(), branchIds, user.getId());
            return ResponseEntity.ok(token);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("로그인 오류", e);
            return ResponseEntity.internalServerError().body("서버 오류");
        }
    }
    
}
