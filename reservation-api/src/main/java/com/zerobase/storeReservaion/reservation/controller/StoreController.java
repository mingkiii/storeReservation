package com.zerobase.storeReservaion.reservation.controller;

import com.zerobase.storeReservaion.reservation.domain.dto.ReservationDto;
import com.zerobase.storeReservaion.reservation.domain.dto.ReviewDto;
import com.zerobase.storeReservaion.reservation.domain.dto.StoreDto;
import com.zerobase.storeReservaion.reservation.domain.dto.StoreInfoDto;
import com.zerobase.storeReservaion.reservation.service.ReservationService;
import com.zerobase.storeReservaion.reservation.service.ReviewService;
import com.zerobase.storeReservaion.reservation.service.StoreService;
import java.util.List;
import java.util.stream.Collectors;
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
    private final ReservationService reservationService;
    private final ReviewService reviewService;

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

    @GetMapping("/getInfo")
    public ResponseEntity<StoreInfoDto> getInfo(
        @RequestParam("id") Long storeId
    ) {
        return ResponseEntity.ok(storeService.getInfo(storeId));
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDto>> getReservations(
        @RequestParam("id") Long storeId
    ) {
        return ResponseEntity.ok(
            reservationService.getReservations(storeId).stream()
                .map(ReservationDto::from).collect(Collectors.toList())
        );
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDto>> getReviews(
        @RequestParam("id") Long storeId
    ) {
        return ResponseEntity.ok(
            reviewService.getReviews(storeId).stream()
                .map(ReviewDto::from).collect(Collectors.toList())
        );
    }
}
