package com.zerobase.storeReservaion.reservation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    LOGIN_REQUIRED(HttpStatus.BAD_REQUEST, "로그인이 필요합니다."),
    WRONG_ACCESS(HttpStatus.BAD_REQUEST, "잘못된 접근입니다."),
    REQUEST_FAIL_FULL_RESERVATION(HttpStatus.BAD_REQUEST, "예약 만료. 해당 시간은 예약이 다 찼습니다."),
    NOT_FOUND_PARTNER(HttpStatus.BAD_REQUEST, "해당 Partner 를 찾을 수 없습니다."),
    NOT_FOUND_STORE(HttpStatus.BAD_REQUEST, "상점을 찾을 수 없습니다."),
    SAME_STORE_NAME(HttpStatus.BAD_REQUEST, "상점명 중복입니다."),
    WRONG_ADDRESS(HttpStatus.BAD_REQUEST, "잘못된 주소입니다.주소를 확인해주세요.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
