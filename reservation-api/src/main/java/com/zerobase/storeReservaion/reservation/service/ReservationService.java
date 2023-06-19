package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_FOUND_RESERVATION;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_FOUND_STORE;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_YOUR_STORE_RESERVATION;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.REQUEST_FAIL_FULL_RESERVATION;

import com.zerobase.storeReservaion.reservation.domain.form.ReservationForm;
import com.zerobase.storeReservaion.reservation.domain.model.Reservation;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
import com.zerobase.storeReservaion.reservation.domain.repository.ReservationRepository;
import com.zerobase.storeReservaion.reservation.domain.repository.StoreRepository;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    // 최대 예약 건수
    private static final int MAX_RESERVATION_COUNT = 2;

    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;

    // 예약 요청
    public String request(Long userId, ReservationForm form) {
        Store store = storeRepository.findById(form.getStoreId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_STORE));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // form으로 받은 string 타입인 dateTime을 LocalDateTime으로 변형
        LocalDateTime requestedDateTime = LocalDateTime.parse(form.getDateTime(), formatter);

        // 요청 예약 시간 1시간 전후로 예약 건수 확인
        LocalDateTime startDateTime = requestedDateTime.minusHours(1);
        LocalDateTime endDateTime = requestedDateTime.plusHours(1);

        long count = reservationRepository.countByDateTimeGreaterThanAndDateTimeLessThanAndStore(startDateTime, endDateTime, store);
        // 이미 예약 건수가 최대 예약 건수면 요청 실패
        if (count == MAX_RESERVATION_COUNT) {
            throw new CustomException(REQUEST_FAIL_FULL_RESERVATION);
        }

        reservationRepository.save(Reservation.of(userId, requestedDateTime, store));

        return "예약 신청 완료. 승인 후 이용 가능합니다.";
    }

    // 상점의 예약 목록 가져오기
    public List<Reservation> getReservations(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_STORE));
        // 날짜를 기준으로 정렬한 목록 (내림차순, 최근 날짜 우선)
        return reservationRepository.findByStoreOrderByDateTimeDesc(store);
    }

    // 파트너 - 예약 승인 변경
    public String changeApproval(Long partnerId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_RESERVATION));

        // 예약된 상점의 파트너 아이디와 메서드를 사용하려는 파트너 아이디 비교
        if (!Objects.equals(partnerId, reservation.getStore().getPartnerId())) {
            throw new CustomException(NOT_YOUR_STORE_RESERVATION);
        }
        // 승인상태 false -> true, true -> false 로 변경
        reservation.setApproval(!reservation.isApproval());

        reservationRepository.save(reservation);
        return "해당 예약 승인 상태를 변경하였습니다.";
    }
}
