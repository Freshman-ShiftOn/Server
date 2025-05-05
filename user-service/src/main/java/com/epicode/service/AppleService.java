package com.epicode.service;

import com.epicode.domain.User;
import com.epicode.dto.AppleTokenResponse;
import com.epicode.dto.AppleUserInfo;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.repository.UserBranchRepository;
import com.epicode.repository.UserRepository;
import com.epicode.security.JwtUtil;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleService {
    private final RestTemplate restTemplate;
    private final Environment env;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserBranchRepository userBranchRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public String authenticateWithApple(String code) {
        try {
            if (code == null || code.isEmpty()) {
                throw new CustomException(ErrorCode.TOKEN_ERROR);
            }

            // 1. client_secret 생성
            String clientSecret = generateClientSecret();

            // 2. 토큰 요청
            AppleTokenResponse tokenResponse = getTokenFromApple(code, clientSecret);

            // 3. 사용자 정보 추출 (id_token 디코딩)
            AppleUserInfo userInfo = parseIdToken(tokenResponse.getIdToken());

            if (userInfo.getEmail() == null) {
                throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
            }

            // 4. DB에서 사용자 조회 또는 등록
            User user = userRepository.findByEmail(userInfo.getEmail());
            if (user == null) {
                user = new User();
                user.setEmail(userInfo.getEmail());
                user.setName("AppleUser" + userInfo.getId());
                //user.setProvider("APPLE");
                user.setCreatedAt(LocalDateTime.now());
                user = userRepository.save(user);
            }

            // 5. Branch 정보 조회
            List<Long> branches = userBranchRepository.findBranchIdsByUserId(user.getId());

            // 6. JWT 발급
            return jwtUtil.createJwtToken(user, branches);
        } catch (Exception e) {
            throw new RuntimeException("Apple 로그인 인증 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private String generateClientSecret() throws Exception {
        String teamId = env.getProperty("apple.team-id");
        String clientId = env.getProperty("spring.security.oauth2.client.registration.apple.client-id");
        String keyId = env.getProperty("apple.key-id");
        String privateKeyPath = env.getProperty("apple.private-key");

        String privateKeyContent = loadPrivateKey(privateKeyPath);

        Date now = new Date();
        Date exp = new Date(now.getTime() + 3600 * 1000); // 1시간 유효

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(teamId)
                .issueTime(now)
                .expirationTime(exp)
                .audience("https://appleid.apple.com")
                .subject(clientId)
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .keyID(keyId)
                .build();

        ECPrivateKey privateKey = (ECPrivateKey) loadECPrivateKey(privateKeyContent);
        JWSSigner signer = new ECDSASigner(privateKey);

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private AppleTokenResponse getTokenFromApple(String code, String clientSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", env.getProperty("spring.security.oauth2.client.registration.apple.redirect-uri"));
        params.add("client_id", env.getProperty("spring.security.oauth2.client.registration.apple.client-id"));
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<AppleTokenResponse> response = restTemplate.exchange(
                "https://appleid.apple.com/auth/token",
                HttpMethod.POST,
                request,
                AppleTokenResponse.class
        );

        return response.getBody();
    }

    private AppleUserInfo parseIdToken(String idToken) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(idToken);
        JWTClaimsSet claims = jwt.getJWTClaimsSet();

        String email = claims.getStringClaim("email"); // null일 수 있음
        String sub = claims.getSubject(); // Apple 고유 ID

        return new AppleUserInfo(sub, email);
    }

    private String loadPrivateKey(String path) throws IOException {
        Resource resource = new ClassPathResource(path.replace("classpath:", ""));
        return new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);
    }

    private PrivateKey loadECPrivateKey(String keyContent) throws Exception {
        String key = keyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePrivate(spec);
    }
}
