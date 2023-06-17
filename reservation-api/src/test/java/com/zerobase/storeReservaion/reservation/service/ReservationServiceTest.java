package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.REQUEST_FAIL_FULL_RESERVATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.storeReservaion.reservation.domain.form.ReservationForm;
import com.zerobase.storeReservaion.reservation.domain.model.Reservation;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
import com.zerobase.storeReservaion.reservation.domain.repository.ReservationRepository;
import com.zerobase.storeReservaion.reservation.domain.repository.StoreRepository;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    public void testRequestReservation_Success() {
        // Given
        Long userId = 1L;
        ReservationForm form = ReservationForm.builder()
            .storeId(1L)
            .dateTime("2023-06-17 10:00")
            .build();
        Store store = Store.builder()
            .id(1L)
            .build();

        when(storeRepository.findById(form.getStoreId())).thenReturn(
            Optional.ofNullable(store));
        when(
            reservationRepository.countByDateTimeGreaterThanAndDateTimeLessThanAndStore(
                any(LocalDateTime.class), any(LocalDateTime.class),
                any(Store.class))
        ).thenReturn(0L);

        // When
        String result = reservationService.requestReservation(userId, form);

        // Then
        verify(reservationRepository, times(1))
            .countByDateTimeGreaterThanAndDateTimeLessThanAndStore(
                any(LocalDateTime.class), any(LocalDateTime.class),
                any(Store.class));
        verify(reservationRepository, times(1))
            .save(any(Reservation.class));
        assertEquals("예약 신청 완료. 승인 후 이용 가능합니다.", result);
    }

    @Test
    public void testRequestReservation_Fail() {
        // Given
        Long userId = 1L;
        ReservationForm form = ReservationForm.builder()
            .storeId(1L)
            .dateTime("2023-06-17 10:00")
            .build();
        Store store = Store.builder()
            .id(1L)
            .build();

        long maxCount = 2; // 예약 건수가 2개 (최대 예약 건수에 도달)
        when(storeRepository.findById(form.getStoreId())).thenReturn(
            Optional.ofNullable(store));
        when(
            reservationRepository.countByDateTimeGreaterThanAndDateTimeLessThanAndStore(
                any(LocalDateTime.class), any(LocalDateTime.class),
                any(Store.class))
        ).thenReturn(maxCount);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> reservationService.requestReservation(userId, form));

        // Then
        verify(reservationRepository, times(1))
            .countByDateTimeGreaterThanAndDateTimeLessThanAndStore(
                any(LocalDateTime.class), any(LocalDateTime.class),
                any(Store.class));
        verify(reservationRepository, never()).save(any());

        assertEquals(REQUEST_FAIL_FULL_RESERVATION, exception.getErrorCode());
    }
}