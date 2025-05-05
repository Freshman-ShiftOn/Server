package com.epicode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppleUserInfo {
    private String id;     // Apple 고유 사용자 ID (sub)
    private String email;  // Apple에서 내려주는 이메일
}