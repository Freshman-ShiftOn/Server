package com.epicode.service;
import com.epicode.dto.SalaryRequestDTO;
import com.epicode.dto.SalaryResponseDTO;
import com.epicode.dto.SpecificTimeSalaryResponseDTO;
import com.epicode.dto.WeeklySalaryDto;

import java.util.List;

public interface SalaryService {
    SalaryResponseDTO createOrUpdateSalary(Long userId, SalaryRequestDTO salaryRequestDTO); // 기본 및 특별 시급 생성/수정
    SalaryResponseDTO getSalaryByUserAndBranch(Long userId, Long branchId); // 기본 및 특별 시급 조회
    void deleteSpecificTimeSalary(Long userId, Long branchId, Long specificTimeSalaryId);

    SalaryResponseDTO getBasicSalary(Long userId, Long branchId); // 기본 시급 조회
    List<SpecificTimeSalaryResponseDTO> getSpecificSalaries(Long userId, Long branchId); // 특별 시급 조회

    List<WeeklySalaryDto> getSalaryInfo(Long branchId, int month);
    List<WeeklySalaryDto> getMonthSalaryInfo(Long branchId, int month);
}