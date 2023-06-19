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

    @GetMapping("/search") // 요청 받은 주소와 가까운 상점 조회
    public ResponseEntity<Page<StoreDto>> getNearStores( // 페이징으로 받음
        @RequestParam("address") String address, // 요청 주소
        @RequestParam("maxDistance") double maxDistance, // 요청 주소 반경 거리(단위 : m)
        @RequestParam(value = "sort", required = false) String sortProperty // 정렬 기준
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

    @GetMapping("/getInfo") // 상점 상세 정보 조회
    public ResponseEntity<StoreInfoDto> getInfo(
        @RequestParam("id") Long storeId
    ) {
        return ResponseEntity.ok(storeService.getInfo(storeId));
    }

    @GetMapping("/reservations") // 상점 예약 목록 조회
    public ResponseEntity<List<ReservationDto>> getReservations(
        @RequestParam("id") Long storeId
    ) {
        return ResponseEntity.ok(
            reservationService.getReservations(storeId).stream()
                .map(ReservationDto::from).collect(Collectors.toList())
        );
    }

    @GetMapping("/reviews") // 상점 리뷰 목록 조회
    public ResponseEntity<List<ReviewDto>> getReviews(
        @RequestParam("id") Long storeId
    ) {
        return ResponseEntity.ok(
            reviewService.getReviews(storeId).stream()
                .map(ReviewDto::from).collect(Collectors.toList())
        );
    }
}
