package com.epicode.manualservice.repository;
import com.epicode.manualservice.model.Favorite;
import com.epicode.manualservice.model.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    List<Favorite> findByUserIdAndBranchId(Long userId, Long branchId);
    Optional<Favorite> findByUserIdAndBranchIdAndManualId(Long userId, Long branchId, Integer manualId);
}