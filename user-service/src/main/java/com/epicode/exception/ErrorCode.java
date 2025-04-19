package com.epicode.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    BRANCH_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 브랜치를 찾을 수 없습니다."),
    USER_BRANCH_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 브랜치에서 유저 정보를 찾을 수 없습니다."),
    SALARY_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 브랜치에서 유저 급여 정보를 찾을 수 없습니다."),
    SPECIFIC_SALARY_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 특별 급여 정보를 찾을 수 없습니다."),
    USER_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "사용자가 인증되지 않았습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생했습니다."),
    TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "토큰을 확인할 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입 된 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "가입되지 않은 사용자입니다."),
    USER_PWD_INVALID(HttpStatus.UNAUTHORIZED, "비밀번호 체크 필요"),
    LOGIN_TYPE_ERROR(HttpStatus.NOT_FOUND, "다른 방식 로그인 필요");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}