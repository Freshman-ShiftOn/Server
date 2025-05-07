package com.epicode.service;

import com.epicode.domain.Branch;
import com.epicode.domain.Salary;
import com.epicode.domain.SpecificTimeSalary;
import com.epicode.dto.SalaryRequestDTO;
import com.epicode.dto.SalaryResponseDTO;
import com.epicode.dto.SpecificTimeSalaryResponseDTO;
import com.epicode.exception.CustomException;
import com.epicode.exception.ErrorCode;
import com.epicode.repository.BranchRepository;
import com.epicode.repository.SalaryRepository;
import com.epicode.repository.SpecificTimeSalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final SpecificTimeSalaryRepository specificTimeSalaryRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public SalaryResponseDTO createOrUpdateSalary(Long userId, SalaryRequestDTO salaryRequestDTO) {
        // 브랜치 유효성 검사
        Branch branch = branchRepository.findById(salaryRequestDTO.getBranchId())
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        // 기본 시급 데이터 조회 또는 생성
        Salary salary = salaryRepository.findByUserIdAndBranchId(userId, salaryRequestDTO.getBranchId())
                .orElseGet(() -> Salary.builder()
                        .userId(userId)
                        .branchId(salaryRequestDTO.getBranchId())
                        .build());

        // 기본 시급 정보 업데이트
        salary.setBasicSalary(salaryRequestDTO.getBasicSalary());
        salary.setWeeklyAllowance(salaryRequestDTO.getWeeklyAllowance());
        salary.setPaymentDate(salaryRequestDTO.getPaymentDate());
        Salary savedSalary = salaryRepository.save(salary);

        // 기존 특별 시급 삭제
        List<SpecificTimeSalary> existingSpecificSalaries = specificTimeSalaryRepository.findByUserIdAndBranchId(userId, salaryRequestDTO.getBranchId());
        specificTimeSalaryRepository.deleteAll(existingSpecificSalaries);

        // 새로운 특별 시급 추가
        List<SpecificTimeSalary> specificTimeSalaries = salaryRequestDTO.getSpecificTimeSalaries().stream()
                .map(dto -> SpecificTimeSalary.builder()
                        .salary(savedSalary)
                        .specificDays(dto.getSpecificDays())
                        .startTime(dto.getStartTime())
                        .endTime(dto.getEndTime())
                        .specificSalary(dto.getSpecificSalary())
                        .build())
                .collect(Collectors.toList());
        specificTimeSalaryRepository.saveAll(specificTimeSalaries);

        return toSalaryResponseDTO(savedSalary, branch.getName(), specificTimeSalaries);
    }

    @Override
    public SalaryResponseDTO getSalaryByUserAndBranch(Long userId, Long branchId) {
        // 기본 시급 조회
        Salary salary = salaryRepository.findByUserIdAndBranchId(userId, branchId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        // 브랜치 정보 조회
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        // 특별 시급 정보 조회
        List<SpecificTimeSalary> specificTimeSalaries = specificTimeSalaryRepository.findByUserIdAndBranchId(userId, branchId);

        return toSalaryResponseDTO(salary, branch.getName(), specificTimeSalaries);
    }

    private SalaryResponseDTO toSalaryResponseDTO(Salary salary, String branchName, List<SpecificTimeSalary> specificTimeSalaries) {
        return SalaryResponseDTO.builder()
                .userId(salary.getUserId())
                .branchId(salary.getBranchId())
                .branchName(branchName)
                .basicSalary(salary.getBasicSalary())
                .weeklyAllowance(salary.getWeeklyAllowance())
                .paymentDate(salary.getPaymentDate())
                .specificTimeSalaries(specificTimeSalaries.stream()
                        .map(s -> SpecificTimeSalaryResponseDTO.builder()
                                .branchName(branchName)
                                .specificDays(s.getSpecificDays())
                                .startTime(s.getStartTime())
                                .endTime(s.getEndTime())
                                .specificSalary(s.getSpecificSalary())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public SalaryResponseDTO getBasicSalary(Long userId, Long branchId) {
        Salary salary = salaryRepository.findByUserIdAndBranchId(userId, branchId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        return SalaryResponseDTO.builder()
                .userId(salary.getUserId())
                .branchId(salary.getBranchId())
                .branchName(branch.getName())
                .basicSalary(salary.getBasicSalary())
                .paymentDate(salary.getPaymentDate())
                .weeklyAllowance(salary.getWeeklyAllowance())
                .build();
    }

    @Override
    public List<SpecificTimeSalaryResponseDTO> getSpecificSalaries(Long userId, Long branchId) {
        // Branch 유효성 확인
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        // 특정 사용자와 지점에 연결된 특별 시급 조회
        List<SpecificTimeSalary> specificTimeSalaries = specificTimeSalaryRepository.findByUserIdAndBranchId(userId, branchId);

        if (specificTimeSalaries.isEmpty()) {
            throw new CustomException(ErrorCode.SALARY_NOT_FOUND);
        }

        return specificTimeSalaries.stream()
                .map(s -> SpecificTimeSalaryResponseDTO.builder()
                        .branchName(branch.getName())
                        .specificDays(s.getSpecificDays())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .specificSalary(s.getSpecificSalary())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSpecificTimeSalary(Long userId, Long branchId, Long specificTimeSalaryId) {
        // 기본 시급 데이터 확인
        Salary salary = salaryRepository.findByUserIdAndBranchId(userId, branchId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALARY_NOT_FOUND));

        // 특별 시급 데이터 확인
        SpecificTimeSalary specificTimeSalary = specificTimeSalaryRepository.findById(specificTimeSalaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.SPECIFIC_SALARY_NOT_FOUND));

        // 특정 특별 시급이 해당 Salary에 속하는지 확인
        if (!specificTimeSalary.getSalary().equals(salary)) {
            throw new CustomException(ErrorCode.SPECIFIC_SALARY_NOT_FOUND);
        }

        // 특별 시급 삭제
        specificTimeSalaryRepository.delete(specificTimeSalary);
    }
}
