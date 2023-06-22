package com.zerobase.storeReservaion.reservation.controller;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.LOGIN_REQUIRED;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.WRONG_ACCESS;

import com.zerobase.storeReservaion.reservation.exception.CustomException;
import com.zerobase.storeReservaion.reservation.service.ReservationService;
import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerReservationController {
    private static final String TOKEN = "X-AUTH-TOKEN";

    private final JwtAuthenticationProvider provider;
    private final ReservationService reservationService;

    // 파트너 - 유저의 예약 요청에 대한 승인
    @PutMapping("/reservation/approval")
    public ResponseEntity<String> reservationApproval(
        @RequestHeader(name = TOKEN) String token,
        @RequestParam("id") Long reservationId
    ) {
        validateToken(token);
        return ResponseEntity.ok(
            reservationService.approval(
                getMemberId(token), reservationId));
    }

    // 파트너 - 유저의 예약 요청에 대한 거절
    @PutMapping("/reservation/refuse")
    public ResponseEntity<String> reservationRefuse(
        @RequestHeader(name = TOKEN) String token,
        @RequestParam("id") Long reservationId
    ) {
        validateToken(token);
        return ResponseEntity.ok(
            reservationService.refuse(
                getMemberId(token), reservationId));
    }

    // 유저가 체크인하면 키오스크로부터 알림을 받는 기능..체크인 시 키오스크클라이언트가 이 서버로 알리기위해 만듬..
    @PostMapping("/notifications/checkin")
    public ResponseEntity<String> handleCheckinNotification(
        @RequestParam Long reservationId,
        @RequestParam Long storeId
    ) {
        // 알림 요청을 처리하는 로직 구현..
        // 필요한 처리 및 응답 작업 수행..
        return ResponseEntity.ok("Notification received");
    }

    private void validateToken(String token) {
        if (token.isEmpty()) {
            throw new CustomException(LOGIN_REQUIRED);
        }
        MemberType memberType = provider.getMemberType(token);
        if (memberType != MemberType.PARTNER) {
            throw new CustomException(WRONG_ACCESS);
        }
    }

    private Long getMemberId(String token) {
        return provider.getMemberVo(token).getId();
    }
}
