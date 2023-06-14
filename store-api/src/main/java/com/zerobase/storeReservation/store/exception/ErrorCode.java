package com.zerobase.storeReservation.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND_PARTNER(HttpStatus.BAD_REQUEST, "해당 Partner 를 찾을 수 없습니다."),
    SAME_STORE_NAME(HttpStatus.BAD_REQUEST, "상점명 중복입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String detail;
}
