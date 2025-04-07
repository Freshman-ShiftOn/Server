package com.epicode.service;
import com.epicode.domain.Boss;
import com.epicode.domain.User;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.repository.*;
import com.epicode.repository.UserRepository;
import com.epicode.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final RestTemplate restTemplate; // RestTemplate Bean 주입
    private final Environment env;
    private final UserRepository userRepository;
    private final BossRepository bossRepository;
    private final UserBranchRepository userBranchRepository;
    private final BossBranchRepository bossBranchRepository;
    private final JwtUtil jwtUtil;

//    public void redirectToKakao(HttpServletResponse response) {
//        try {
//            String clientId = env.getProperty("spring.security.oauth2.client.registration.kakao.client-id");
//            String redirectUri = env.getProperty("spring.security.oauth2.client.registration.kakao.redirect-uri");
//
//            if (clientId == null || redirectUri == null) {
//                throw new IllegalStateException("카카오 로그인에 필요한 환경 변수가 설정되지 않았습니다.");
//            }
//
//            String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize"
//                    + "?client_id=" + clientId
//                    + "&redirect_uri=" + redirectUri
//                    + "&response_type=code";
//
//            response.sendRedirect(kakaoLoginUrl);
//        } catch (Exception e) {
//            throw new RuntimeException("카카오 리다이렉트 처리 중 오류: " + e.getMessage(), e);
//        }
//    }

    @Transactional
    public void saveUser(User user) {//,String name
        if(userRepository.existsByEmail(user.getEmail())){
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
        userRepository.save(user);
    }
    


//AccessCode로부터 받아오기 버전(Web)-> return JWT
   public String authenticateWithKakao(String code) {
       try {
           String accessToken = getAccessTokenFromKakao(code);
           if (accessToken == null || accessToken.isEmpty()) {
               throw new CustomException(ErrorCode.TOKEN_ERROR);
           }

            Boss boss = getBossInfoFromKakao(accessToken);
            String userEmail = boss.getEmail();
           if (userEmail == null || userEmail.isEmpty()) {
               throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
           }
           

           if (!bossRepository.existsByEmail(userEmail)) {
                saveBoss(boss);
           }

           Long bossId = bossRepository.findByEmail(userEmail).getId();
           List<Long> branches = bossBranchRepository.findBranchIdsByBossId(bossId);

           return jwtUtil.generateToken(userEmail, branches, bossId); // JWT 토큰 생성
       } catch (Exception e) {
           throw new RuntimeException("Auth code로 access token 발급을 시도했으나 실패함: " + e.getMessage(), e);
       }
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

    private User getUserInfoFromKakao(String accessToken) {
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
                User kakaoUser = new User();// 카카오 계정 정보 가져오기
                Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");

                // 프로필 정보 가져오기
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

                // 이메일 추출
                String email = (String) kakaoAccount.get("email");

                // 닉네임 추출 (프로필이 있는 경우)
                String nickname = (profile != null) ? (String) profile.get("nickname") : "닉네임 없음";

                // 사용자 고유 ID 추출
                //Long userId = (Long) response.getBody().get("id");

                // 유저 객체에 값 설정
                kakaoUser.setEmail(email != null ? email : "이메일 없음");
                kakaoUser.setName(nickname);
                //kakaoUser.setId(userId);
                return kakaoUser;
            } else {
                throw new RuntimeException("Failed to fetch user info from Kakao: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오 유저 정보 요청 중 오류: " + e.getMessage());
        }
    }

    private Boss getBossInfoFromKakao(String accessToken) {
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
                Boss kakaoUser = new Boss();// 카카오 계정 정보 가져오기
                Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");

                // 프로필 정보 가져오기
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

                // 이메일 추출
                String email = (String) kakaoAccount.get("email");

                // 닉네임 추출 (프로필이 있는 경우)
                String nickname = (profile != null) ? (String) profile.get("nickname") : "닉네임 없음";

                // 사용자 고유 ID 추출
                //Long userId = (Long) response.getBody().get("id");

                // 유저 객체에 값 설정
                kakaoUser.setEmail(email != null ? email : "이메일 없음");
                kakaoUser.setName(nickname);
                //kakaoUser.setId(userId);
                return kakaoUser;
            } else {
                throw new RuntimeException("Failed to fetch user info from Kakao: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오 유저 정보 요청 중 오류: " + e.getMessage());
        }
    }

    @Transactional
    public void saveBoss(Boss boss) {//,String name
        if(bossRepository.existsByEmail(boss.getEmail())){
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
        bossRepository.save(boss);
    }

    //AT -> (없으면 생성) -> 이메일 리턴
    @Transactional
    public String authenticateWebKakao(String accessToken) {
        try {
            if (accessToken == null || accessToken.isEmpty()) {
                throw new CustomException(ErrorCode.TOKEN_ERROR);
            }

            // 카카오에서 사용자 정보 가져오기 (저장은 하지 않음)
            User kakaoUser = getUserInfoFromKakao(accessToken);
            kakaoUser.setCreatedAt(LocalDateTime.now());

            if (kakaoUser == null) {
                throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
            }

            // 이메일로 사용자 검색
            User existingUser = userRepository.findByEmail(kakaoUser.getEmail());

            if (existingUser == null) {
                // 데이터베이스에 저장하면서 ID 할당
                existingUser = userRepository.save(kakaoUser); // 여기서 영속화되어 ID가 생성됨
            }

            // 사용자 Branch 정보 가져오기
            List<Long> branches = userBranchRepository.findBranchIdsByUserId(existingUser.getId());

            // JWT 토큰 생성
            return jwtUtil.createJwtToken(existingUser, branches);
        } catch (Exception e) {
            throw new RuntimeException("Auth code로 access token 발급을 시도했으나 실패함: " + e.getMessage(), e);
        }
    }






    ///////////////////////////
// //just이메일 리턴
//    private String getEmailInfoFromKakao(String accessToken) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + accessToken);

//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            ResponseEntity<Map> response = restTemplate.exchange(
//                    env.getProperty("spring.security.oauth2.client.provider.kakao.user-info-uri"),
//                    HttpMethod.GET,
//                    entity,
//                    Map.class
//            );

//            if (response.getStatusCode() == HttpStatus.OK) {
//                Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
//                return (String) kakaoAccount.get("email");
//            } else {
//                throw new RuntimeException("Failed to fetch user info from Kakao: " + response.getStatusCode());
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("카카오 유저 정보 요청 중 오류: " + e.getMessage());
//        }
//    }

}