package com.zerobase.storeReservation.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // signUp
    ALREADY_REGISTER_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원 입니다."),

    // signIn
    LOGIN_FAIL(HttpStatus.BAD_REQUEST, "로그인에 실패하였습니다."),

    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "올바른 입력값이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
