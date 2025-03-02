package com.epicode.repository;


import com.epicode.domain.Branch;
import com.epicode.domain.User;
import com.epicode.domain.UserBranch;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBranchRepository extends JpaRepository<UserBranch, Long> {
    @Query("SELECT ub.branch.id FROM UserBranch ub WHERE ub.user.id = :userId")
    List<Long> findBranchIdsByUserId(@Param("userId") Long userId);
    
}