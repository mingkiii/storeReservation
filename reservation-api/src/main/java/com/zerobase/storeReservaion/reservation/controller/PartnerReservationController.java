package com.zerobase.storeReservaion.reservation.controller;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.LOGIN_REQUIRED;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.WRONG_ACCESS;

import com.zerobase.storeReservaion.reservation.exception.CustomException;
import com.zerobase.storeReservaion.reservation.service.ReservationService;
import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerReservationController {

    private final JwtAuthenticationProvider provider;
    private final ReservationService reservationService;

    @PutMapping("/reservation")
    public ResponseEntity<String> responseReservation(
        @RequestHeader(name = "X-AUTH-TOKEN") String token,
        @RequestParam("id") Long reservationId
    ) {
        if (token.isEmpty()) {
            throw new CustomException(LOGIN_REQUIRED);
        }
        MemberType memberType = provider.getMemberType(token);
        if (memberType != MemberType.PARTNER) {
            throw new CustomException(WRONG_ACCESS);
        }
        return ResponseEntity.ok(
            reservationService.changeReservationApproval(
                provider.getMemberVo(token).getId(), reservationId));
    }
}
