package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_FOUND_STORE;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.SAME_STORE_NAME;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.WRONG_ADDRESS;

import com.zerobase.storeReservaion.reservation.domain.dto.StoreDto;
import com.zerobase.storeReservaion.reservation.domain.dto.StoreInfoDto;
import com.zerobase.storeReservaion.reservation.domain.form.AddStoreForm;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
import com.zerobase.storeReservaion.reservation.domain.repository.StoreRepository;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import com.zerobase.storeReservaion.reservation.util.GeocodingUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final GeocodingUtil geocodingUtil;

    @Transactional
    public Store create(Long partnerId, AddStoreForm form) {
        Optional<Store> existingStore = storeRepository.findByPartnerIdAndName(
            partnerId, form.getName());
        // 이미 파트너의 상점 중 상점명이 있는 경우
        if (existingStore.isPresent()) {
            throw new CustomException(SAME_STORE_NAME);
        }

        double[] coords = getCoordinatesFromAddress(form.getAddress());
        double latitude = coords[0];
        double longitude = coords[1];

        return storeRepository.save(
            Store.of(partnerId, form, latitude, longitude));
    }

    /**
     * 주어진 좌표 주변에 있는 가게 목록을 가져옵니다.
     *
     * @param address       요청 주소
     * @param maxDistance   요청 주소 반경 최대 거리 (미터 단위)
     * @param page          페이지 번호 (1부터 시작)
     * @param pageSize      페이지 당 결과 수
     * @param sort          정렬 기준
     * @return              주어진 조건에 맞는 상점 목록과 페이징 정보
     */
    public Page<StoreDto> getNearbyStores(
        String address, double maxDistance, int page, int pageSize, Sort sort) {

        double[] coords = getCoordinatesFromAddress(address);
        double latitude = coords[0];
        double longitude = coords[1];

        return storeRepository.getNearbyStores(
            latitude, longitude, maxDistance, page, pageSize, sort);
    }

    // 상점 상세 정보 가져오기
    public StoreInfoDto getInfo(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_STORE));

        return StoreInfoDto.from(store);
    }

    // 오픈 api 이용하여 주소를 위도, 경도로 변환
    private double[] getCoordinatesFromAddress(String address) {
        double[] coords = geocodingUtil.geoCoding(address);
        if (coords == null) {
            throw new CustomException(WRONG_ADDRESS);
        }
        return coords;
    }
}
