package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.DIFFERENT_STORE;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_CHECKIN_AVAILABLE_TIME;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_FOUND_RESERVATION;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.RESERVATION_NOT_APPROVED;

import com.zerobase.storeReservaion.reservation.client.KioskClient;
import com.zerobase.storeReservaion.reservation.domain.model.Reservation;
import com.zerobase.storeReservaion.reservation.domain.repository.ReservationRepository;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KioskService {

    private final ReservationRepository reservationRepository;
    private final KioskClient kioskClient;

    // 키오스크 기능 - 체크인 가능한 예약 정보인지 검사
    @Transactional
    public String checkValid(Long reservationId, Long storeId, LocalDateTime currentTime) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_RESERVATION));

        // 승인상태 false -> 예외 발생
        if (!reservation.isApproval()) {
            throw new CustomException(RESERVATION_NOT_APPROVED);
        }

        // 예약 정보의 상점아이디와 상점에 있는 키오스크의 상점아이디가 다르면 예외 발생
        if (!reservation.getStore().getId().equals(storeId)) {
            throw new CustomException(DIFFERENT_STORE);
        }

        // 예약시간 10분전에만 체크인 가능하도록. 10분전의 경계는 체크인되도록 minusMinutes(9)로 설정함
        if (!currentTime.isBefore(reservation.getDateTime().minusMinutes(9))) {
            throw new CustomException(NOT_CHECKIN_AVAILABLE_TIME);
        }
        reservation.setCheckIn(true);
        reservationRepository.save(reservation);

        kioskClient.sendCheckinNotification(reservationId, storeId);

        return "예약 체크인이 완료되었습니다.";
    }
}
