package com.epicode.service;
import com.epicode.domain.Boss;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.repository.BossRepository;
import com.epicode.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BossService {
    private final BossRepository bossRepository;

    
    @Transactional
    public void saveBoss(Boss boss) {//,String name
        if(bossRepository.existsByEmail(boss.getEmail())){
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
        bossRepository.save(boss);
    }
    @Transactional
    public void deleteUser(String userEmail) {//회원 탈퇴
        if(!bossRepository.existsByEmail(userEmail)){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        bossRepository.deleteByEmail(userEmail);
    }

}