package com.epicode.repository;

import com.epicode.dto.BranchIdNameProjection;
import com.epicode.dto.WorkerProjection;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.model.UserBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBranchRepository extends JpaRepository<UserBranch, Long> {
    @Query("SELECT b.id AS id, b.name AS name FROM Branch b WHERE b.id IN (SELECT ub.branch.id FROM UserBranch ub WHERE ub.user.id = :userId)")
    List<BranchIdNameProjection> findBranchIdsAndNamesByUserId(Long userId);
//    @Query("SELECT b.name FROM Branch b WHERE b.id IN :branchIds")
//    List<String> findBranchNamesByIds(@Param("branchIds") List<Long> branchIds);
    @Query("SELECT u.id AS id, u.name AS name " +
            "FROM UserBranch ub JOIN ub.user u WHERE ub.branch.id = :branchId")
    List<WorkerProjection> findWorkersByBranchId(Long branchId);
    boolean existsByUserIdAndBranchId(Long userId, Long branchId);
    void deleteByUserAndBranch(User user, Branch branch);

    Optional<UserBranch> findByUserAndBranch(User user, Branch branch);
}