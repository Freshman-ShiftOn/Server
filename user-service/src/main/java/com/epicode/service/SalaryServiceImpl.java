package com.epicode.service;

import com.epicode.domain.Branch;
import com.epicode.domain.Salary;
import com.epicode.domain.User;
import com.epicode.dto.SalaryRequestDTO;
import com.epicode.dto.SalaryResponseDTO;
import com.epicode.exception.SalaryException;
import com.epicode.repository.BranchRepository;
import com.epicode.repository.SalaryRepository;
import com.epicode.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @Transactional
    @Override
    public void createOrUpdateSalary(Long userId, SalaryRequestDTO salaryRequestDTO) {
        userRepository.findById(userId)
                .orElseThrow(() -> new SalaryException("User not found with ID " + userId));
        branchRepository.findById(salaryRequestDTO.getBranchId())
                .orElseThrow(() -> new SalaryException("Branch not found with ID " + salaryRequestDTO.getBranchId()));

        // 데이터 조회 및 업데이트/삽입 처리
        salaryRepository.findByUserIdAndBranchId(userId, salaryRequestDTO.getBranchId()).ifPresentOrElse(
                existingSalary -> {
                    // 데이터가 존재하면 업데이트
                    System.out.println("Existing salary found: " + existingSalary);
                    existingSalary.setBasicSalary(salaryRequestDTO.getBasicSalary());
                    existingSalary.setSpecificDays(salaryRequestDTO.getSpecificDays());
                    existingSalary.setStartTime(salaryRequestDTO.getStartTime());
                    existingSalary.setEndTime(salaryRequestDTO.getEndTime());
                    existingSalary.setSpecificSalary(salaryRequestDTO.getSpecificSalary());
                    existingSalary.setWeeklyAllowance(salaryRequestDTO.getWeeklyAllowance());
                    existingSalary.setPaymentDate(salaryRequestDTO.getPaymentDate());
                    salaryRepository.saveAndFlush(existingSalary); // 업데이트
                },
                () -> {
                    // 데이터가 없으면 새로 생성
                    Salary newSalary = Salary.builder()
                            .userId(userId)
                            .branchId(salaryRequestDTO.getBranchId())
                            .basicSalary(salaryRequestDTO.getBasicSalary())
                            .specificDays(salaryRequestDTO.getSpecificDays())
                            .startTime(salaryRequestDTO.getStartTime())
                            .endTime(salaryRequestDTO.getEndTime())
                            .specificSalary(salaryRequestDTO.getSpecificSalary())
                            .weeklyAllowance(salaryRequestDTO.getWeeklyAllowance())
                            .paymentDate(salaryRequestDTO.getPaymentDate())
                            .build();
                    salaryRepository.saveAndFlush(newSalary); // 삽입
                }
        );
    }

    @Override
    public Salary getSalaryByUserAndBranch(Long userId, Long branchId) {
        // 특정 사용자와 Branch의 Salary 조회
        return salaryRepository.findByUserIdAndBranchId(userId, branchId)
                .orElseThrow(() -> new SalaryException("Salary not found for user ID " + userId + " and branch ID " + branchId));
    }

    @Override
    public List<SalaryResponseDTO> getSalariesByBranch(Long branchId) {
        return salaryRepository.findAllByBranchId(branchId).stream().map(salary -> {
            SalaryResponseDTO dto = new SalaryResponseDTO();
            dto.setUserId(salary.getUserId());
            dto.setBranchId(salary.getBranchId());
            dto.setBasicSalary(salary.getBasicSalary());
            dto.setSpecificDays(salary.getSpecificDays());
            dto.setStartTime(salary.getStartTime());
            dto.setEndTime(salary.getEndTime());
            dto.setSpecificSalary(salary.getSpecificSalary());
            dto.setWeeklyAllowance(salary.getWeeklyAllowance());
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public void deleteSalary(Long branchId, Long userId) {
        // Salary 조회
        Salary salary = salaryRepository.findByUserIdAndBranchId(userId, branchId)
                .orElseThrow(() -> new SalaryException("해당하는 Salary가 없습니다. Branch ID: " + branchId + ", User ID: " + userId));

        // 권한 확인
        if (!salary.getUserId().equals(userId)) {
            throw new SalaryException("Unauthorized: You cannot delete this salary.");
        }

        // Salary 삭제
        salaryRepository.delete(salary);
    }

//    @Override
//    public Salary createSalary(Long userId,SalaryRequestDTO salaryRequestDTO) {
//        // User와 Branch가 유효한지 확인
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new SalaryException("User not found with ID " + userId));
//        Branch branch = branchRepository.findById(salaryRequestDTO.getBranchId())
//                .orElseThrow(() -> new SalaryException("Branch not found with ID " + salaryRequestDTO.getBranchId()));
//
//        Salary salary = Salary.builder()
//                .userId(userId)
//                .branchId(salaryRequestDTO.getBranchId())
//                .basicSalary(salaryRequestDTO.getBasicSalary())
//                .specificDays(salaryRequestDTO.getSpecificDays())
//                .startTime(salaryRequestDTO.getStartTime())
//                .endTime(salaryRequestDTO.getEndTime())
//                .specificSalary(salaryRequestDTO.getSpecificSalary())
//                .weeklyAllowance(salaryRequestDTO.getWeeklyAllowance())
//                .paymentDate(salaryRequestDTO.getPaymentDate())
//                .build();
//
//        return salaryRepository.save(salary);
//    }
//    @Override
//    public void updateSalary(Long userId, Long branchId, SalaryRequestDTO salaryRequestDTO) {
//        // User와 Branch가 유효한지 확인
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new SalaryException("User not found with ID " + userId));
//        Branch branch = branchRepository.findById(salaryRequestDTO.getBranchId())
//                .orElseThrow(() -> new SalaryException("Branch not found with ID " + salaryRequestDTO.getBranchId()));
//
//        Salary salary = salaryRepository.findByUserIdAndBranchId(userId,branchId)
//                .orElseThrow(() -> new SalaryException("Salary not found with userId, branchId" + userId + "," + branchId));
//        // Salary 업데이트
//        salary.setUserId(userId);
//        salary.setBranchId(branchId);
//        salary.setBasicSalary(salaryRequestDTO.getBasicSalary());
//        salary.setSpecificDays(salaryRequestDTO.getSpecificDays());
//        salary.setStartTime(salaryRequestDTO.getStartTime());
//        salary.setEndTime(salaryRequestDTO.getEndTime());
//        salary.setSpecificSalary(salaryRequestDTO.getSpecificSalary());
//        salary.setWeeklyAllowance(salaryRequestDTO.getWeeklyAllowance());
//        salary.setPaymentDate(salaryRequestDTO.getPaymentDate());
//
//        // 저장
//        salaryRepository.save(salary);
//    }
}