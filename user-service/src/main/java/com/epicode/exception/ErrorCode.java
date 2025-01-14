package com.epicode.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    BRANCH_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 브랜치를 찾을 수 없습니다."),
    SALARY_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 브랜치에서 유저 급여 정보를 찾을 수 없습니다."),
    SPECIFIC_SALARY_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 특별 급여 정보를 찾을 수 없습니다."),
    USER_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "사용자가 인증되지 않았습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}