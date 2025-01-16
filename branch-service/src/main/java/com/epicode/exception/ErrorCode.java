package com.epicode.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_BRANCH_NAME(HttpStatus.BAD_REQUEST, "브랜치명이 유효하지 않습니다."),
    BRANCH_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 브랜치를 찾을 수 없습니다."),
    USER_BRANCH_EXISTS(HttpStatus.CONFLICT, "이미 가입되어 있는 매장입니다."),
    USER_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "사용자가 인증되지 않았습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "서비스에 가입되지 않은 사용자입니다."),
    USER_NOT_INVITED(HttpStatus.UNAUTHORIZED, "초대받은 사용자와 현재 사용자가 일치하지 않습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}