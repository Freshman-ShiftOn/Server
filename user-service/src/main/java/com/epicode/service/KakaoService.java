package com.epicode.service;
import com.epicode.domain.User;
import com.epicode.repository.UserRepository;
import com.epicode.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final RestTemplate restTemplate; // RestTemplate Bean 주입
    private final Environment env;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public String authenticateWithKakao(String code) {
        try {
            String accessToken = getAccessTokenFromKakao(code);
            if (accessToken == null || accessToken.isEmpty()) {
                throw new IllegalStateException("토큰이 비어있습니다.");
            }

            String userEmail = getUserInfoFromKakao(accessToken);
            if (userEmail == null || userEmail.isEmpty()) {
                throw new IllegalStateException("유저 정보가 비어있습니다.");
            }

            if (!userRepository.existsByEmail(userEmail)) {
                saveNewUser(userEmail);//저장
            }

            return jwtUtil.generateToken(userEmail);
        } catch (Exception e) {
            throw new RuntimeException("Auth code로 access token 발급을 시도했으나 실패함.  " + e.getMessage(), e);
        }
    }
    private void saveNewUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("김크루");//임시 지정
        System.out.println(user);
        userRepository.save(user);
    }

    private String getAccessTokenFromKakao(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", env.getProperty("spring.security.oauth2.client.registration.kakao.client-id"));
            params.add("redirect_uri", env.getProperty("spring.security.oauth2.client.registration.kakao.redirect-uri"));
            params.add("code", code);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    env.getProperty("spring.security.oauth2.client.provider.kakao.token-uri"),
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return (String) response.getBody().get("access_token");
            } else {
                throw new RuntimeException("Failed to fetch access token from Kakao: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오AccessToken 발급 중 오류: " + e.getMessage());
        }
    }

    private String getUserInfoFromKakao(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    env.getProperty("spring.security.oauth2.client.provider.kakao.user-info-uri"),
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
                return (String) kakaoAccount.get("email");
            } else {
                throw new RuntimeException("Failed to fetch user info from Kakao: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오 유저 정보 요청 중 오류: " + e.getMessage());
        }
    }
}