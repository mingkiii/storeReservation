package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_FOUND_RESERVATION;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_YOUR_STORE_RESERVATION;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.REQUEST_FAIL_FULL_RESERVATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
    @DisplayName("유저 - 예약 요청_성공")
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
        String result = reservationService.request(userId, form);

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
    @DisplayName("유저 - 예약 요청_실패")
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
            () -> reservationService.request(userId, form));

        // Then
        verify(reservationRepository, times(1))
            .countByDateTimeGreaterThanAndDateTimeLessThanAndStore(
                any(LocalDateTime.class), any(LocalDateTime.class),
                any(Store.class));
        verify(reservationRepository, never()).save(any());

        assertEquals(REQUEST_FAIL_FULL_RESERVATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("상점 예약 목록 조회")
    void getStoreReservations() {
        //Given
        Long storeId = 1L;
        Store store = Store.builder()
            .id(storeId)
            .name("Store 1")
            .build();
        Reservation reservation1 = Reservation.builder()
            .id(1L)
            .store(store)
            .dateTime(LocalDateTime.of(2023, 6, 17, 12, 0))
            .build();
        Reservation reservation2 = Reservation.builder()
            .id(2L)
            .store(store)
            .dateTime(LocalDateTime.of(2023, 6, 17, 14, 0))
            .build();
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1);
        reservations.add(reservation2);

        when(storeRepository.findById(anyLong())).thenReturn(
            Optional.ofNullable(store));
        when(
            reservationRepository.findByStoreOrderByDateTimeDesc(store)).thenReturn(
            reservations);
        //When
        List<Reservation> result = reservationService.getReservations(
            storeId);
        //Then
        assertEquals(reservations, result);
    }

    @Test
    @DisplayName("예약 승인 상태 변경 - 성공")
    public void changeReservationApproval_Success() {
        // Given
        Long partnerId = 1L;
        Long reservationId = 5L;
        Store store = Store.builder()
            .id(10L)
            .partnerId(partnerId)
            .build();
        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .approval(false)
            .store(store)
            .dateTime(LocalDateTime.now())
            .build();

        when(reservationRepository.findById(reservationId))
            .thenReturn(Optional.ofNullable(reservation));
        when(reservationRepository.save(
            ArgumentMatchers.any(Reservation.class))).thenReturn(reservation);
        // When
        String result =
            reservationService.changeApproval(partnerId, reservationId);

        // Then
        verify(reservationRepository, times(1))
            .findById(reservationId);
        verify(reservationRepository, times(1))
            .save(Objects.requireNonNull(reservation));
        assertTrue(reservation.isApproval());
        assertEquals("해당 예약 승인 상태를 변경하였습니다.", result);
    }

    @Test
    @DisplayName("예약 승인 상태 변경 - 실패_NotFoundReservation")
    public void changeReservationApproval_NotFoundReservation() {
        // Given
        Long partnerId = 1L;
        Long reservationId = 1L;

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // When/Then
        CustomException exception = assertThrows(CustomException.class,
            () -> reservationService.changeApproval(partnerId, reservationId));
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, never()).save(ArgumentMatchers.any(Reservation.class));

        assertEquals(NOT_FOUND_RESERVATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("예약 승인 상태 변경 - 실패_NotYourStoreReservation")
    public void testResponseReservation_NotYourStoreReservation() {
        // Given
        Long partnerId = 1L;
        Long reservationId = 1L;
        Long otherPartnerId = 2L;

        Store store = Store.builder()
            .id(10L)
            .partnerId(otherPartnerId)
            .build();
        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .approval(false)
            .store(store)
            .dateTime(LocalDateTime.now())
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(java.util.Optional.of(reservation));

        // When/Then
        CustomException exception = assertThrows(CustomException.class,
            () -> reservationService.changeApproval(partnerId, reservationId));
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, never()).save(ArgumentMatchers.any(Reservation.class));

        assertEquals(NOT_YOUR_STORE_RESERVATION, exception.getErrorCode());
    }
}