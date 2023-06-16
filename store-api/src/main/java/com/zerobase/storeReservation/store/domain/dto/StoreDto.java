package com.zerobase.storeReservation.store.domain.dto;

import com.zerobase.storeReservation.store.domain.model.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    private Long id;
    private String name;
    private String address;
    private String text;
    private double rating;
    private Long reviewCount;
    private int distance;

    public static StoreDto from(Store store) {
        return StoreDto.builder()
            .id(store.getId())
            .name(store.getName())
            .address(store.getAddress())
            .text(store.getText())
            .build();
    }
    public static StoreDto toStoreDto(Store store) {
        return StoreDto.builder()
            .id(store.getId())
            .name(store.getName())
            .address(store.getAddress())
            .text(store.getText())
            .rating(store.getRating())
            .reviewCount(store.getReviewCount())
            .build();
    }
}
