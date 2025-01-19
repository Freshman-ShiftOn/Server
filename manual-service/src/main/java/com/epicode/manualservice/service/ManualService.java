package com.epicode.manualservice.service;

import com.epicode.manualservice.config.BranchClient;
import com.epicode.manualservice.dto.ManualDTO;
import com.epicode.manualservice.dto.ManualTaskDTO;
import com.epicode.manualservice.exception.CustomException;
import com.epicode.manualservice.exception.ErrorCode;
import com.epicode.manualservice.model.Favorite;
import com.epicode.manualservice.model.Manual;
import com.epicode.manualservice.model.ManualTask;
import com.epicode.manualservice.repository.FavoriteRepository;
import com.epicode.manualservice.repository.ManualRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ManualService {
    private final ManualRepository manualRepository;
    private final ManualTaskService manualTaskService;
    private final FavoriteRepository favoriteRepository;
    private final BranchClient branchClient;


    public void validateBranchAccess(String jwtToken,Integer branchId) {
        List<String> branchIdList = getBranchesForUser(jwtToken);
        System.out.println(branchIdList);
        if (!branchIdList.contains(String.valueOf(branchId))) {
            throw new CustomException(ErrorCode.USER_BRANCH_NOT_EXISTS);//404
        }
    }
    public List<String> getBranchesForUser(String jwtToken) {
        return branchClient.getBranchList(jwtToken);
    }

    // 매뉴얼 목록 조회
//    @Transactional(readOnly = true)
//    public List<ManualDTO> getManualsByBranchId(Integer branchId) {
//        List<Manual> manuals = manualRepository.findByBranchId(branchId);
//        if (manuals.isEmpty()) {
//            //log.info("Branch ID {}에 대한 매뉴얼이 없습니다.", branchId);
//            return Collections.emptyList();
//        }
//        return manuals.stream()
//                .map(manual -> new ManualDTO().toManualDTO(manual))
//                .collect(Collectors.toList());
//    }
    // 매뉴얼 목록 조회(즐겨찾기 상태 포함)
    @Transactional(readOnly = true)
    public List<ManualDTO> getManualsByBranchId(Long userId, Integer branchId) {
        List<Manual> manuals = manualRepository.findByBranchId(branchId);
        if (manuals.isEmpty()) {
            return Collections.emptyList();
        }
        List<Favorite> favorites = favoriteRepository.findByUserIdAndBranchId(userId, Long.valueOf(branchId));

        // 즐겨찾기 상태를 매뉴얼에 매핑
        List<Integer> favoriteManualIds = favorites.stream()
                .filter(Favorite::isFavorite)
                .map(Favorite::getManualId)
                .collect(Collectors.toList());

        return manuals.stream()
                .map(manual -> {
                    ManualDTO manualDTO = new ManualDTO().toManualDTO(manual);
                    manualDTO.setFavorite(favoriteManualIds.contains(manual.getId()));
                    return manualDTO;
                })
                .collect(Collectors.toList());
    }

    //즐겨찾기 업데이트
    @Transactional
    public void updateFavorite(Long userId, Integer branchId, Integer manualId, boolean isFavorite) {
        Favorite favorite = favoriteRepository.findByUserIdAndBranchIdAndManualId(userId, Long.valueOf(branchId), manualId)
                .orElse(new Favorite(userId, Long.valueOf(branchId), manualId, false));
        favorite.setFavorite(isFavorite);
        favoriteRepository.save(favorite);
    }

    // 매뉴얼 상세 조회 (태스크 포함)
    @Transactional(readOnly = true)
    public ManualDTO getManualByIdAndBranchId(Integer id, Integer branchId) {
        Manual manual = manualRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MANUAL_NOT_FOUND));

        if (!manual.getBranchId().equals(branchId)) {
            throw new CustomException(ErrorCode.TASK_NOT_FOUND);
        }

        // ManualTaskDTO 리스트 생성
        List<ManualTaskDTO> tasks = manualTaskService.getManualTasksByManualId(id);

        // Manual → ManualDTO 변환 후 태스크 설정
        ManualDTO manualDTO = new ManualDTO().toManualDTO(manual);
        manualDTO.setTasks(tasks);

        return manualDTO;
    }

    // 매뉴얼 생성
    public ManualDTO createManual(ManualDTO manualDTO) {
        Manual manual = manualDTO.toManualEntity(); // ManualDTO → Manual 변환
        Manual savedManual = manualRepository.save(manual);
        return new ManualDTO().toManualDTO(savedManual); // Manual → ManualDTO 변환
    }

    // 매뉴얼 수정
    public ManualDTO updateManual(Integer manualId, ManualDTO manualDTO) {
        Manual existingManual = manualRepository.findById(manualId)
                .orElseThrow(() -> new CustomException(ErrorCode.MANUAL_NOT_FOUND));

        // 기존 필드 업데이트
        existingManual.setBranchId(manualDTO.getBranchId());
        existingManual.setTitle(manualDTO.getTitle());
        existingManual.setWorkerId(manualDTO.getWorkerId());

        // 기존 tasks 제거 및 새 tasks 추가, 부모 엔티티를 저장하면 연관된 자식 엔티티도 자동으로 저장
        if (manualDTO.getTasks() != null) {
            existingManual.getTasks().clear();

            // 새로 전달된 tasks를 추가
            List<ManualTask> updatedTasks = manualDTO.getTasks().stream()
                    .map(taskDTO -> taskDTO.toTaskEntity(taskDTO, existingManual)) // DTO → Entity 변환
                    .collect(Collectors.toList());

            existingManual.getTasks().addAll(updatedTasks);
        }

        // Manual 저장
        Manual updatedManual = manualRepository.save(existingManual);
        return new ManualDTO().toManualDTO(updatedManual); // Entity → DTO 변환
    }

    // 매뉴얼 삭제
    public void deleteManual(Integer manualId) {
        Manual manual = manualRepository.findById(manualId)
                .orElseThrow(() -> new CustomException(ErrorCode.MANUAL_NOT_FOUND));
        manualRepository.delete(manual);
    }

    //인증 정보에서 branchList 받아온 버전 : 재로그인 이슈
//    public void validateBranchAccess(String branches, Integer branchId) {
//
//        //System.out.println("validate branchId: " + branchId+", branches: "+branches);
//        List<Integer> branchIdList = Arrays.stream(branches.split(","))
//                .map(Integer::valueOf) // 문자열을 Integer로 변환
//                .toList();
//        //System.out.println("list branchIds: "+branchIdList.toString());
//        if (!branchIdList.contains(branchId)) {
//            throw new CustomException(ErrorCode.USER_BRANCH_NOT_EXISTS);//404
//        }
//    }
}
