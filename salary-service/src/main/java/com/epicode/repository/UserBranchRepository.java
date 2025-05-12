package com.epicode.repository;

import com.epicode.domain.UserBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface UserBranchRepository extends JpaRepository<UserBranch, Long> {
    @Query("SELECT ub FROM UserBranch ub WHERE ub.user.id = :userId AND ub.branch.id = :branchId")
    UserBranch findByUserIdAndBranchId(@Param("userId") Long userId, @Param("branchId") Long branchId);
}