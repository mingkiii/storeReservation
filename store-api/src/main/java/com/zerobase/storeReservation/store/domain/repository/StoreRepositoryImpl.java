package com.zerobase.storeReservation.store.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.storeReservation.store.domain.dto.StoreDto;
import com.zerobase.storeReservation.store.domain.model.QStore;
import com.zerobase.storeReservation.store.domain.model.Store;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<StoreDto> getNearbyStores(double latitude, double longitude,
        double maxDistance, int page, int pageSize, Sort sort) {
        QStore qStore = QStore.store;
        BooleanBuilder whereBuilder = new BooleanBuilder();

        double maxDistanceInDegrees = maxDistance / 111000.0;
        whereBuilder.and(
            qStore.latitude.between(latitude - maxDistanceInDegrees, latitude + maxDistanceInDegrees)
                .and(qStore.longitude.between(longitude - maxDistanceInDegrees, longitude + maxDistanceInDegrees))
        );

        JPAQuery<Store> query = queryFactory.select(qStore).from(qStore).where(whereBuilder);

        if (sort != null) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                switch (property) {
                    case "name":
                        query.orderBy(qStore.name.asc());
                        break;
                    case "rating":
                        query.orderBy(qStore.rating.desc());
                        break;
                    default:
                        query.orderBy(qStore.id.asc());
                        break;
                }
            }
        }

        query.offset((long) (page - 1) * pageSize).limit(pageSize);

        List<Store> stores = query.fetch();

        long totalCount = query.fetchCount();

        List<StoreDto> storeDtos = stores.stream()
            .map(store -> {
                int distance = calculateDistance(latitude, longitude, store.getLatitude(), store.getLongitude());
                StoreDto storeDto = StoreDto.toStoreDto(store);
                storeDto.setDistance(distance);
                return storeDto;
            })
            .collect(Collectors.toList());
        if (sort != null) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                if (property.equals("distance")) {
                    storeDtos.sort(Comparator.comparingDouble(StoreDto::getDistance));
                }
            }
        }

        return new PageImpl<>(storeDtos, PageRequest.of(page - 1, pageSize), totalCount);
    }
    private int calculateDistance(double lat1, double lon1,
        double lat2, double lon2) {
        double earthRadius = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(
            Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (earthRadius * c * 1000);

    }
}
