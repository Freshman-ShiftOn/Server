package com.epicode.service;
import com.epicode.model.Branch;
import com.epicode.model.UserBranch;
import com.epicode.repository.BranchRepository;
import com.epicode.repository.UserBranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final UserBranchRepository userBranchRepository;
    private final BranchRepository branchRepository;

    public List<String> getBranchNamesByUserId(Long userId) {
        //UserBranch에서 branchId 목록 조회
        List<Long> branchIds = userBranchRepository.findBranchIdsByUserId(userId);
        //Branch에서 branchName 목록 조회
        return branchRepository.findBranchNamesByIds(branchIds);
    }
}
