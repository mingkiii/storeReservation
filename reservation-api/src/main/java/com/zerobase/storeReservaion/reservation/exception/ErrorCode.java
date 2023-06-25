package com.zerobase.storeReservaion.reservation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    LOGIN_REQUIRED(HttpStatus.BAD_REQUEST, "로그인이 필요합니다."),
    WRONG_ACCESS(HttpStatus.BAD_REQUEST, "잘못된 접근입니다."),

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "올바른 입력값이 아닙니다."),

    NOT_YOUR_STORE_RESERVATION(HttpStatus.BAD_REQUEST, "해당 예약의 점주만 접근 가능합니다."),
    ALREADY_CHECKIN_RESERVATION(HttpStatus.BAD_REQUEST, "이미 사용된 예약입니다."),
    REQUEST_FAIL_FULL_RESERVATION(HttpStatus.BAD_REQUEST, "예약 만료. 해당 시간은 예약이 다 찼습니다."),

    NOT_FOUND_PARTNER(HttpStatus.BAD_REQUEST, "해당 Partner 를 찾을 수 없습니다."),
    NOT_FOUND_STORE(HttpStatus.BAD_REQUEST, "상점을 찾을 수 없습니다."),
    NOT_FOUND_RESERVATION(HttpStatus.BAD_REQUEST, "예약을 찾을 수 없습니다."),

    SAME_STORE_NAME(HttpStatus.BAD_REQUEST, "상점명 중복입니다."),

    DIFFERENT_STORE(HttpStatus.BAD_REQUEST, "예약된 상점과 다른 상점입니다."),
    DIFFERENT_USERID(HttpStatus.BAD_REQUEST, "유저 정보와 예약 정보가 다릅니다."),
    RESERVATION_NOT_VALID(HttpStatus.BAD_REQUEST, "예약이 거절되었거나 이미 방문했던 예약입니다."),
    NOT_CHECKIN_AVAILABLE_TIME(HttpStatus.BAD_REQUEST, "예약된 시간 10분 전에만 체크인 가능합니다."),

    NOT_CHECKIN_RESERVATION(HttpStatus.BAD_REQUEST, "예약된 상점을 이용 후 리뷰 등록 가능합니다."),

    WRONG_ADDRESS(HttpStatus.BAD_REQUEST, "잘못된 주소입니다.주소를 확인해주세요."),

    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "토큰형식이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
