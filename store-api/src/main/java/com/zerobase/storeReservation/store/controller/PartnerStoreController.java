package com.zerobase.storeReservation.store.controller;

import static com.zerobase.storeReservation.store.exception.ErrorCode.WRONG_ACCESS;

import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import com.zerobase.storeReservation.store.domain.form.AddStoreForm;
import com.zerobase.storeReservation.store.domain.dto.StoreDto;
import com.zerobase.storeReservation.store.exception.CustomException;
import com.zerobase.storeReservation.store.service.StoreService;
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

    private final StoreService storeService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<StoreDto> addStore(
        @RequestHeader(name = "X-AUTH-TOKEN") String token,
        @RequestBody AddStoreForm form
    ) {
        MemberType memberType = provider.getMemberType(token);
        if (memberType == MemberType.USER) {
            throw new CustomException(WRONG_ACCESS);
        }
        return ResponseEntity.ok(
            StoreDto.from(
                storeService.addStore(
                    provider.getMemberVo(token).getId(), form)));
    }
}
