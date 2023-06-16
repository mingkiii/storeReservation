package com.zerobase.storeReservation.store.controller;

import com.zerobase.storeReservation.store.domain.dto.StoreDto;
import com.zerobase.storeReservation.store.domain.dto.StoreInfoDto;
import com.zerobase.storeReservation.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/search")
    public ResponseEntity<Page<StoreDto>> getNearStores(
        @RequestParam("address") String address,
        @RequestParam("maxDistance") double maxDistance,
        @RequestParam(value = "sort", required = false) String sortProperty
    ) {
        Sort sort = sortProperty != null ? Sort.by(sortProperty) : null;
        int page = 1;
        int pageSize = 5;

        return ResponseEntity.ok(
            storeService.getNearbyStores(
                address, maxDistance, page, pageSize, sort
            )
        );
    }

    @GetMapping
    public ResponseEntity<StoreInfoDto> getStoreDetails(@RequestParam("id") Long storeId) {
        return ResponseEntity.ok(storeService.getStoreDetails(storeId));
    }
}
