package com.zerobase.storeReservaion.reservation.domain.form;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddStoreForm {

    @NotBlank(message = "필수 입력")
    private String name;
    @NotBlank(message = "필수 입력, 지번 주소로 입력해주세요.")
    private String address;

    private String text;
}
