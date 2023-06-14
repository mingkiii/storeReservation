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
    LOGIN_CHECK_FAIL(HttpStatus.BAD_REQUEST, "이메일과 비밀번호를 확인 해주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}
