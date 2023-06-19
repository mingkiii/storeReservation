package com.zerobase.storeReservaion.reservation.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.storeReservaion.reservation.domain.dto.StoreDto;
import com.zerobase.storeReservaion.reservation.domain.model.QStore;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
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
    // Q 클래스 이용
    private final JPAQueryFactory queryFactory;
    /**
     * 주어진 좌표 주변에 있는 가게 목록을 가져옵니다.
     *
     * @param latitude      요청 주소의 위도
     * @param longitude     요청 주소의 경도
     * @param maxDistance   최대 거리 (미터 단위)
     * @param page          페이지 번호 (1부터 시작)
     * @param pageSize      페이지 당 결과 수
     * @param sort          정렬 기준
     * @return              주어진 조건에 맞는 상점 목록과 페이징 정보
     */
    @Override
    public Page<StoreDto> getNearbyStores(double latitude, double longitude,
        double maxDistance, int page, int pageSize, Sort sort) {
        QStore qStore = QStore.store;
        BooleanBuilder whereBuilder = new BooleanBuilder();

        // 최대 거리를 도 단위에서 위도와 경도 단위로 변환하여 적용
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
                    case "name": // 상점명순(오름차순)으로 정렬
                        query.orderBy(qStore.name.asc());
                        break;
                    case "rating": // 별점순(내림차순)으로 정렬
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
                // 주어진 좌표와 상점의 좌표 사이의 거리를 계산
                int distance = calculateDistance(latitude, longitude, store.getLatitude(), store.getLongitude());
                StoreDto storeDto = StoreDto.from(store);
                storeDto.setDistance(distance);
                return storeDto; // storeDto로 변형 후 계산된 거리 set
            })
            .collect(Collectors.toList());
        // 그 후 정렬 기준이 거리일 경우(오름차순)
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

    // 거리 계산 메서드
    /**
     * 두 좌표 사이의 거리를 계산합니다.
     *
     * @param lat1  첫 번째 위치의 위도
     * @param lon1  첫 번째 위치의 경도
     * @param lat2  두 번째 위치의 위도
     * @param lon2  두 번째 위치의 경도
     * @return      두 좌표 사이의 거리 (미터 단위)
     */
    private int calculateDistance(double lat1, double lon1, double lat2, double lon2) {
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
