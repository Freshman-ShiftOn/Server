package com.epicode.repository;
import com.epicode.domain.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository extends JpaRepository<UserCredentials, Long> {
    Optional<UserCredentials> findByUser_Email(String email);
}
