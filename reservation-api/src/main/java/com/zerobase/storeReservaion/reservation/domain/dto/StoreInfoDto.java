package com.zerobase.storeReservaion.reservation.domain.dto;

import com.zerobase.storeReservaion.reservation.domain.model.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StoreInfoDto {
    private Long id;
    private Long partnerId;
    private String name;
    private String address;
    private String text;
    private double rating;
    private Long reviewCount;

    public static StoreInfoDto from(Store store) {
        return StoreInfoDto.builder()
            .id(store.getId())
            .partnerId(store.getPartnerId())
            .name(store.getName())
            .address(store.getAddress())
            .text(store.getText())
            .rating(store.getRating())
            .reviewCount(store.getReviewCount())
            .build();
    }
}
