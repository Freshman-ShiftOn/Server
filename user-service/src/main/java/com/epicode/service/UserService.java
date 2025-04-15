package com.epicode.service;
import com.epicode.domain.User;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.repository.UserBranchRepository;
import com.epicode.repository.UserRepository;
import com.epicode.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void saveUser(User user) {//,String name
        if(userRepository.existsByEmail(user.getEmail())){
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
        userRepository.save(user);
    }
    @Transactional
    public User updateUserName(Long userId, String newName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(newName);
        return userRepository.save(user);
    }
    @Transactional
    public void deleteUser(String userEmail) {//회원 탈퇴
        if(!userRepository.existsByEmail(userEmail)){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteByEmail(userEmail);
    }

}