package com.example.calendarservice.dto;

import com.example.calendarservice.model.ShiftRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftRequestsByBranchMonthResponseDto {
    private List<ShiftRequest> myRequests;
    private List<ShiftRequest> othersRequests;
}