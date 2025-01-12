package com.epicode.service;

import com.epicode.domain.Salary;
import com.epicode.dto.SalaryRequestDTO;
import com.epicode.dto.SalaryResponseDTO;

import java.util.List;

public interface SalaryService {
    void createOrUpdateSalary(Long userId, SalaryRequestDTO salaryRequestDTO);
//    Salary createSalary(Long userId,SalaryRequestDTO salaryRequestDTO);
//    void updateSalary(Long userId, Long branchId, SalaryRequestDTO salaryRequestDTO);
    Salary getSalaryByUserAndBranch(Long userId, Long branchId);// 사용자와 매장에 따른 Salary 조회
    List<SalaryResponseDTO> getSalariesByBranch(Long branchId); // 특정 Branch의 Salary 리스트 조회
    void deleteSalary(Long salaryId, Long userId);
}