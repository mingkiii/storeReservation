package com.zerobase.storeReservaion.reservation.domain.form;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewForm {
    @DecimalMin(value = "1.0", inclusive = true, message = "최소 1.0 점")
    @DecimalMax(value = "5.0", inclusive = true, message = "최대 5.0 점")
    @Digits(integer = 1, fraction = 1, message = "소수점은 0.5 단위로만 가능합니다.")
    @Positive(message = "양수값만 가능합니다.")
    private double rating;

    @NotBlank
    private String text;
}
