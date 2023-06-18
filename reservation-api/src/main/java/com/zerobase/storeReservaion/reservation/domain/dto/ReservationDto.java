package com.zerobase.storeReservaion.reservation.domain.dto;

import com.zerobase.storeReservaion.reservation.domain.model.Reservation;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
import java.time.LocalDateTime;
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
public class ReservationDto {
    private Long id;
    private Store store;
    private LocalDateTime dateTime;
    private boolean approval;

    public static ReservationDto from(Reservation reservation) {
        return ReservationDto.builder()
            .id(reservation.getId())
            .store(reservation.getStore())
            .dateTime(reservation.getDateTime())
            .approval(reservation.isApproval())
            .build();
    }
}
