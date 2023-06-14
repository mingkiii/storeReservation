package com.zerobase.storeReservation.store.controller;

import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.store.domain.AddStoreForm;
import com.zerobase.storeReservation.store.domain.StoreDto;
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
        return ResponseEntity.ok(
            StoreDto.from(
                storeService.addStore(
                    provider.getMemberVo(token).getId(), form)));
    }
}
