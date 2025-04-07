package com.epicode.repository;
import com.epicode.domain.Boss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BossRepository extends JpaRepository<Boss, Long>{
    boolean existsByEmail(String email);
    Boss findByEmail(String email);
    void deleteByEmail(String email);//회원 탈퇴
}