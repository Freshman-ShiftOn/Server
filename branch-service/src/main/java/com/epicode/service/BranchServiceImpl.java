package com.epicode.service;
import com.epicode.dto.BranchIdNameProjection;
import com.epicode.dto.BranchRequestDTO;
import com.epicode.dto.WorkerDTO;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.model.Branch;
import com.epicode.model.User;
import com.epicode.model.UserBranch;
import com.epicode.repository.BranchRepository;
import com.epicode.repository.UserBranchRepository;
import com.epicode.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final UserBranchRepository userBranchRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;


    // userId 기반 branchName 조회
//    @Override
//    public List<String> getBranchNamesByUserId(Long userId) {
//        System.out.println(userId + "유저의 브랜치를 불러옵니다!");
//        //UserBranch에서 branchId 목록 조회
//        List<Long> branchIds = userBranchRepository.findBranchIdsByUserId(userId);
//        //Branch에서 branchName 목록 조회
//        System.out.println(branchRepository.findBranchNamesByIds(branchIds));
//        return branchRepository.findBranchNamesByIds(branchIds);
//    }
    @Override
    public List<BranchIdNameProjection> getBranchesByUserId(Long userId) {
        return userBranchRepository.findBranchIdsAndNamesByUserId(userId);
    }

    @Override
    public String getBranchNameByBranchId(Long branchId) {
        return branchRepository.findNameById(branchId).getName();
    }
//
//    //branchIds기반 branchName 조회
//    @Override
//    public List<String> getBranchNamesByUserIds(Long[] branchIds) {
//        //branchIds -> branchName 목록 조회
//        return branchRepository.findBranchNamesByIds(List.of(branchIds));
//    }
    // branchName 기반 branchId 조회
    @Override
    public Long getBranchIdByName(String name) {
        Branch b = branchRepository.findIdByName(name);
        if(b==null){
            throw new CustomException(ErrorCode.BRANCH_NOT_FOUND);
        }
        return b.getId();
    }

    @Override
    @Transactional
    public Long createBranch(BranchRequestDTO dto) {
        User user = userRepository.findIdByEmail(dto.getEmail());

        if (user == null || !user.getId().equals(dto.getUserId())) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
        }

        //Branch 저장
        if(branchRepository.existsByName(dto.getName())){
            throw new CustomException(ErrorCode.INVALID_BRANCH_NAME);
        }
        Branch requestBranch = new Branch();
        requestBranch.setName(dto.getName());
        requestBranch.setAdress(dto.getAdress());
        requestBranch.setBasic_cost(dto.getBasic_cost());
        requestBranch.setDial_numbers(dto.getDial_numbers());
        requestBranch.setWeekly_allowance(dto.getWeekly_allowance());
        Branch savedBranch = branchRepository.save(requestBranch);

        UserBranch userBranch = new UserBranch();
        userBranch.setUser(user);
        userBranch.setBranch(savedBranch);
        userBranch.setRoles("사장");
        userBranch.setJoinedAt(LocalDateTime.now());

        //UserBranch 저장
        UserBranch branch = userBranchRepository.save(userBranch);
        return branch.getBranch().getId();
    }

//    @Override
//    public List<WorkerProjection> getWorkersByBranchId(Long branchId) {
//        if (branchId == null) {
//            throw new CustomException(ErrorCode.BRANCH_NOT_FOUND);
//        }
//        return userBranchRepository.findWorkersByBranchId(branchId);
//    }

    @Override
    public List<WorkerDTO> getWorkersByBranchId(Long branchId) {
        if (branchId == null) {
            throw new CustomException(ErrorCode.BRANCH_NOT_FOUND);
        }
        return userBranchRepository.findWorkersByBranchId(branchId);
    }

    @Override
    public Branch getBranchProfile(Long branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new CustomException(ErrorCode.BRANCH_NOT_FOUND));
    }
}
