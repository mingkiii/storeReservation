package com.zerobase.storeReservaion.reservation.domain.form;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationForm {
    private Long storeId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotBlank(message = "필수 입력")
    private String dateTime;
}
