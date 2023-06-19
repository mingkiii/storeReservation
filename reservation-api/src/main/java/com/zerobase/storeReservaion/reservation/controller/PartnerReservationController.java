package com.zerobase.storeReservaion.reservation.controller;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.LOGIN_REQUIRED;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.WRONG_ACCESS;

import com.zerobase.storeReservaion.reservation.exception.CustomException;
import com.zerobase.storeReservaion.reservation.service.ReservationService;
import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PutMapping("/reservation")
    public ResponseEntity<String> responseReservation( // 파트너 - 유저의 예약 요청에 대한 승인 여부
        @RequestHeader(name = TOKEN) String token,
        @RequestParam("id") Long reservationId
    ) {
        validateToken(token);
        return ResponseEntity.ok(
            reservationService.changeApproval(
                getMemberId(token), reservationId));
    }

    @PutMapping("/{storeId}/checkin") // 키오스크 기능 - 방문 가능한 예약 정보인지 검사 후 체크인
    public ResponseEntity<String> checkValidReservation(
        @PathVariable Long storeId,
        @RequestParam("id") Long reservationId
    ) {
        LocalDateTime currentTime = LocalDateTime.now();
        return ResponseEntity.ok(
            reservationService.checkValid(reservationId, storeId, currentTime)
        );
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
