package com.epicode.repository;


import com.epicode.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findIdByEmail(String email);
    Optional<User> findByEmail(String email);
}