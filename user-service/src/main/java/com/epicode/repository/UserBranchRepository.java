package com.epicode.repository;


import com.epicode.domain.UserBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBranchRepository extends JpaRepository<UserBranch, Long> {
    @Query("SELECT ub.branch.id FROM UserBranch ub WHERE ub.user.id = :userId")
    Long[] findBranchIdsByUserId(@Param("userId") Long userId);

}