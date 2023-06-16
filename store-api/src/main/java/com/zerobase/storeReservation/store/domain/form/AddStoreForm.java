package com.zerobase.storeReservation.store.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddStoreForm {

    private String name;
    private String address;
    private String text;
}
