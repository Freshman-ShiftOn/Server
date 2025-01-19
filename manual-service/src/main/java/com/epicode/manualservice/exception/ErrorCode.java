package com.epicode.manualservice.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_BRANCH_NAME(HttpStatus.BAD_REQUEST, "브랜치명이 유효하지 않습니다."),
    BRANCH_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 브랜치를 찾을 수 없습니다."),
    MANUAL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 매뉴얼을 찾을 수 없습니다."),
    INVALID_MANUAL_ID(HttpStatus.NOT_FOUND, "해당 매뉴얼을 찾을 수 없습니다."),
    TASK_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 매장에 해당 매뉴얼이 존재하지 않습니다."),
    USER_BRANCH_NOT_EXISTS(HttpStatus.NOT_FOUND, "사용자가 가입되어있지 않은 매장입니다."),
    USER_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "사용자가 인증되지 않았습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "서비스에 가입되지 않은 사용자입니다.");
    //SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}