package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.DIFFERENT_STORE;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_CHECKIN_AVAILABLE_TIME;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.RESERVATION_NOT_APPROVED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.storeReservaion.reservation.client.KioskClient;
import com.zerobase.storeReservaion.reservation.domain.model.Reservation;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
import com.zerobase.storeReservaion.reservation.domain.repository.ReservationRepository;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KioskServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private KioskClient kioskClient;

    @InjectMocks
    private KioskService kioskService;

    @Test
    public void testCheckValid_Success() {
        // Given
        Long reservationId = 1L;
        Long storeId = 1L;
        LocalDateTime currentTime = LocalDateTime.of(2023,6,18,13,50);

        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .approval(true)
            .store(Store.builder().id(storeId).build())
            .dateTime(LocalDateTime.of(2023,6,18,14,0))
            .checkIn(false)
            .build();

        // Mock 객체 설정
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // 테스트 수행
        String result = kioskService.checkValid(1L, 1L, currentTime);

        // 결과 확인
        assertEquals("예약 체크인이 완료되었습니다.", result);
        assertTrue(reservation.isCheckIn());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(kioskClient, times(1)).sendCheckinNotification(1L, 1L);
    }

    @Test
    @DisplayName("예약 체크인 - 실패_ 승인 되지 않은 예약")
    public void testCheckValidReservation_ReservationNotApproved() {
        // Given
        Long reservationId = 1L;
        Long storeId = 1L;
        LocalDateTime currentTime = LocalDateTime.of(2023,6,18,13,50);

        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .approval(false) // 예약이 승인되지 않은 상태로 설정
            .store(Store.builder().id(storeId).build())
            .dateTime(LocalDateTime.of(2023,6,18,14,0))
            .checkIn(false)
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When/Then
        CustomException exception = assertThrows(CustomException.class,
            () -> kioskService.checkValid(reservationId, storeId, currentTime));

        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, never()).save(any(Reservation.class));

        assertEquals(RESERVATION_NOT_APPROVED, exception.getErrorCode());
        assertFalse(reservation.isCheckIn());
    }

    @Test
    @DisplayName("예약 체크인 - 실패_예약된 상점과 다른 상점")
    public void testCheckValidReservation_DifferentStore() {
        // Given
        Long reservationId = 1L;
        Long storeId = 1L;
        Long otherStoreId = 2L;
        LocalDateTime currentTime = LocalDateTime.of(2023,6,18,13,50);

        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .approval(true)
            .store(Store.builder().id(otherStoreId).build())
            .dateTime(LocalDateTime.of(2023,6,18,14,0))
            .checkIn(false)
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When/ Then
        CustomException exception = assertThrows(CustomException.class,
            () -> kioskService.checkValid(reservationId, storeId, currentTime));

        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, never()).save(any(Reservation.class));

        assertEquals(DIFFERENT_STORE, exception.getErrorCode());
        assertFalse(reservation.isCheckIn());
    }

    @Test
    @DisplayName("예약 체크인 - 실패_예약시간 10분 전보다 지난 시간에 체크인 시도")
    public void testCheckValidReservation_NotCheckInAvailableTime() {
        // Given
        Long reservationId = 1L;
        Long storeId = 1L;
        LocalDateTime currentTime = LocalDateTime.of(2023,6,18,13,55);

        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .approval(true)
            .store(Store.builder().id(storeId).build())
            .dateTime(LocalDateTime.of(2023,6,18,14,0))
            .checkIn(false)
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When/ Then
        CustomException exception = assertThrows(CustomException.class,
            () -> kioskService.checkValid(reservationId, storeId, currentTime));

        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, never()).save(any(Reservation.class));

        assertEquals(NOT_CHECKIN_AVAILABLE_TIME, exception.getErrorCode());
        assertFalse(reservation.isCheckIn());
    }
}