package com.zerobase.storeReservation.store.service;

import static com.zerobase.storeReservation.store.exception.ErrorCode.NOT_FOUND_STORE;
import static com.zerobase.storeReservation.store.exception.ErrorCode.SAME_STORE_NAME;
import static com.zerobase.storeReservation.store.exception.ErrorCode.WRONG_ADDRESS;

import com.zerobase.storeReservation.store.domain.dto.StoreDto;
import com.zerobase.storeReservation.store.domain.dto.StoreInfoDto;
import com.zerobase.storeReservation.store.domain.form.AddStoreForm;
import com.zerobase.storeReservation.store.domain.model.Store;
import com.zerobase.storeReservation.store.domain.repository.StoreRepository;
import com.zerobase.storeReservation.store.exception.CustomException;
import com.zerobase.storeReservation.store.util.GeocodingUtil;
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
    public Store addStore(Long partnerId, AddStoreForm form) {
        Optional<Store> existingStore = storeRepository.findByPartnerIdAndName(
            partnerId, form.getName());
        if (existingStore.isPresent()) {
            throw new CustomException(SAME_STORE_NAME);
        }
        double[] coords = geocodingUtil.geoCoding(form.getAddress());

        return storeRepository.save(
            Store.of(partnerId, form, coords[0], coords[1]));
    }

    public Page<StoreDto> getNearbyStores(
        String address, double maxDistance, int page, int pageSize, Sort sort) {

        double[] coords = geocodingUtil.geoCoding(address);
        if (coords == null) {
            throw new CustomException(WRONG_ADDRESS);
        }
        double latitude = coords[0];
        double longitude = coords[1];

        return storeRepository.getNearbyStores(
            latitude, longitude, maxDistance, page, pageSize, sort);
    }

    public StoreInfoDto getStoreDetails(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_STORE));

        return StoreInfoDto.from(store);
    }
}
