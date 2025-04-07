package com.epicode.repository;

import com.epicode.domain.BossBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BossBranchRepository extends JpaRepository<BossBranch, Long> {
    @Query("SELECT ub.branch.id FROM BossBranch ub WHERE ub.boss.id = :bossId")
    List<Long> findBranchIdsByBossId(@Param("bossId") Long bossId);
    
}