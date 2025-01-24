package com.epicode.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.epicode.domain.User;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.repository.UserRepository;
import com.epicode.security.KakaoKeyProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoJWTService {
    private final UserRepository userRepository;
    private static final String ISSUER = "https://kauth.kakao.com"; // JWT 발급자
    private final KakaoKeyProvider keyProvider;

    public DecodedJWT verifyIdToken(String idToken) {
        try {
            // JWT 디코딩
            DecodedJWT jwtOrigin = JWT.decode(idToken);

            // RSA256 알고리즘 설정
            Algorithm algorithm = Algorithm.RSA256(keyProvider);

            // JWT 검증기 생성
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://kauth.kakao.com") // 발급자 검증
                    .build();

            // 검증 및 디코딩된 JWT 반환
            return verifier.verify(idToken);
        } catch (com.auth0.jwt.exceptions.TokenExpiredException e) {
            throw new RuntimeException("ID 토큰이 만료되었습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired JWT token", e);
        }
    }

    public User saveUser(DecodedJWT jwt) {
        String kakaoId = jwt.getSubject(); // 사용자 고유 ID
        String email = jwt.getClaim("email").asString(); // 이메일 (사용자 동의 필요)
        String nickname = jwt.getClaim("nickname").asString(); // 닉네임

        if (kakaoId == null || kakaoId.isEmpty()) {
            throw new RuntimeException("Kakao ID is missing in JWT.");
        }

        // kakaoId를 Long으로 변환 가능 여부 확인
        try {
            Long kakaoIdAsLong = Long.valueOf(kakaoId);
            User user = userRepository.findById(kakaoIdAsLong).orElse(new User());
            user.setId(kakaoIdAsLong);
            user.setEmail(email);
            user.setName(nickname);
            return userRepository.save(user);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid kakaoId format: " + kakaoId, e);
        }
    }
    public DecodedJWT authenticateRealKakao(String idToken) {
        try {
            // 1. 카카오 JWT (ID 토큰) 검증
            DecodedJWT decodedJWT = verifyIdToken(idToken);

            // 2. 사용자 이메일 및 고유 ID 확인 (필수 클레임)
            String email = decodedJWT.getClaim("email").asString();
            String nickname = decodedJWT.getClaim("nickname").asString();

            if (email == null || email.isEmpty()) {
                throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
            }

            if (nickname == null || nickname.isEmpty()) {
                nickname = "카카오 사용자"; // 닉네임 기본값
            }

            // 3. 검증된 JWT 반환
            return decodedJWT;
        } catch (Exception e) {
            throw new RuntimeException("카카오 ID 토큰 인증 실패: " + e.getMessage(), e);
        }
    }
}