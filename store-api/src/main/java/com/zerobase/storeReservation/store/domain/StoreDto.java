package com.zerobase.storeReservation.store.domain;

import com.zerobase.storeReservation.store.domain.model.Store;
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
public class StoreDto {
    private Long id;
    private String name;
    private String address;
    private String text;

    public static StoreDto from(Store store) {
        return StoreDto.builder()
            .id(store.getId())
            .name(store.getName())
            .address(store.getAddress())
            .text(store.getText())
            .build();
    }
}
