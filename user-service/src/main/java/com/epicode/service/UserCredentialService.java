package com.epicode.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.epicode.repository.*;
import com.epicode.dto.SignUpRequestDTO;
import com.epicode.domain.*;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCredentialService {//Web Auth로직
    private final UserCredentialRepository userCredentialRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void registerUser(SignUpRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }//이미 있는 유저 - 에러
        //없다면
        // 1. 유저 저장
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        User savedUser = userRepository.save(user); // ID 생성

        // 2. Credential 저장 (User ID 기반)
        UserCredentials credential = new UserCredentials();
        
        if(!request.getConfirmPassword().equals(request.getPassword())){//2차 검증
            throw new CustomException(ErrorCode.USER_PWD_INVALID);
        }else{
            credential.setUser(savedUser);
            credential.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        userCredentialRepository.save(credential);//해시값 포함 연쇄 저장
        // User와 UserCredential은 같은 트랜잭션 내에서 저장
    }
}
