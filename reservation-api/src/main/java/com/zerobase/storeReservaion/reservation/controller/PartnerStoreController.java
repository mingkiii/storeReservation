package com.zerobase.storeReservaion.reservation.controller;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.LOGIN_REQUIRED;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.WRONG_ACCESS;

import com.zerobase.storeReservaion.reservation.domain.dto.StoreInfoDto;
import com.zerobase.storeReservaion.reservation.domain.form.AddStoreForm;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import com.zerobase.storeReservaion.reservation.service.StoreService;
import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partner/store")
@RequiredArgsConstructor
public class PartnerStoreController {
    private static final String TOKEN = "X-AUTH-TOKEN";

    private final StoreService storeService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<StoreInfoDto> addStore( // 파트너 상점 등록
        @RequestHeader(name = TOKEN) String token,
        @Valid @RequestBody AddStoreForm form
    ) {
        validateToken(token);
        return ResponseEntity.ok(
            StoreInfoDto.from(
                storeService.create(
                    getMemberId(token), form)));
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
