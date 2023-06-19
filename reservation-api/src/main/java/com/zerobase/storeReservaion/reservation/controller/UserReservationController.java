package com.zerobase.storeReservaion.reservation.controller;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.LOGIN_REQUIRED;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.WRONG_ACCESS;

import com.zerobase.storeReservaion.reservation.domain.form.ReservationForm;
import com.zerobase.storeReservaion.reservation.domain.form.ReviewForm;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import com.zerobase.storeReservaion.reservation.service.ReservationService;
import com.zerobase.storeReservaion.reservation.service.ReviewService;
import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/reservation")
@RequiredArgsConstructor
public class UserReservationController {
    private static final String TOKEN = "X-AUTH-TOKEN";
    private final JwtAuthenticationProvider provider;
    private final ReservationService reservationService;
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> requestReservation( // 유저 - 예약 요청
        @RequestHeader(name = TOKEN) String token,
        @RequestBody ReservationForm form
    ) {
        validateToken(token);
        return ResponseEntity.ok(
            reservationService.request(
                getMemberId(token), form
            )
        );
    }

    @PostMapping("/review") // 유저 - 리뷰 작성
    public ResponseEntity<String> createReview(
        @RequestHeader(name = TOKEN) String token,
        @RequestParam("id") Long reservationId,
        @RequestBody ReviewForm form
    ) {
        validateToken(token);
        return ResponseEntity.ok(
            reviewService.create(
                getMemberId(token), token, reservationId, form
            )
        );
    }

    private void validateToken(String token) {
        if (token.isEmpty()) {
            throw new CustomException(LOGIN_REQUIRED);
        }
        MemberType memberType = provider.getMemberType(token);
        if (memberType != MemberType.USER) {
            throw new CustomException(WRONG_ACCESS);
        }
    }

    private Long getMemberId(String token) {
        return provider.getMemberVo(token).getId();
    }
}
