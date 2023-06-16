package com.zerobase.storeReservation.store.domain.repository;

import com.zerobase.storeReservation.store.domain.dto.StoreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public interface StoreRepositoryCustom {
    Page<StoreDto> getNearbyStores(double latitude, double longitude, double maxDistance, int page, int pageSize, Sort sort);
}
