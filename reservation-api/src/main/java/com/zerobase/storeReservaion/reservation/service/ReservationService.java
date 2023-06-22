package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.ALREADY_CHECKIN_RESERVATION;
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
        // form 으로 받은 string 타입인 dateTime 을 LocalDateTime 으로 변형
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

    // 파트너 - 예약 승인
    public String approval(Long partnerId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_RESERVATION));

        // 예약된 상점의 파트너 아이디와 메서드를 사용하려는 파트너 아이디 비교
        if (!Objects.equals(partnerId, reservation.getStore().getPartnerId())) {
            throw new CustomException(NOT_YOUR_STORE_RESERVATION);
        }
        reservation.setApproval(true);
        reservation.setRefuse(false);
        reservationRepository.save(reservation);

        return "해당 예약을 승인하였습니다.";
    }

    // 파트너 - 예약 거절
    public String refuse(Long partnerId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_RESERVATION));

        // 예약된 상점의 파트너 아이디와 메서드를 사용하려는 파트너 아이디 비교
        if (!Objects.equals(partnerId, reservation.getStore().getPartnerId())) {
            throw new CustomException(NOT_YOUR_STORE_RESERVATION);
        }
        // 이미 사용(방문)된 예약일 경우 거절 불가
        if (reservation.isCheckIn()) {
            throw new CustomException(ALREADY_CHECKIN_RESERVATION);
        }
        reservation.setRefuse(true);
        reservation.setApproval(false);
        reservationRepository.save(reservation);

        return "해당 예약을 거절하였습니다.";
    }
}
