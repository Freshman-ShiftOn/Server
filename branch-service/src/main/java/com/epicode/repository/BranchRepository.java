package com.epicode.repository;


import com.epicode.model.Branch;
import com.epicode.model.UserBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    Long findIdByName(String name);

    @Query("SELECT b.name FROM Branch b WHERE b.id IN :branchIds")
    List<String> findBranchNamesByIds(@Param("branchIds") List<Long> branchIds);
}
