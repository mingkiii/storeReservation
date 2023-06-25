package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.DIFFERENT_USERID;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_CHECKIN_RESERVATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.storeReservaion.reservation.client.UserClient;
import com.zerobase.storeReservaion.reservation.client.UserDto;
import com.zerobase.storeReservaion.reservation.domain.form.ReviewForm;
import com.zerobase.storeReservaion.reservation.domain.model.Reservation;
import com.zerobase.storeReservaion.reservation.domain.model.ReservationStatus;
import com.zerobase.storeReservaion.reservation.domain.model.Review;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
import com.zerobase.storeReservaion.reservation.domain.repository.ReservationRepository;
import com.zerobase.storeReservaion.reservation.domain.repository.ReviewRepository;
import com.zerobase.storeReservaion.reservation.domain.repository.StoreRepository;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private UserClient userClient;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 등록 성공")
    public void testRegistrationReview_Success() {
        // Given
        Long userId = 1L;
        String token = "testToken";
        String userName = "홍길동";
        Long reservationId = 1L;
        ReviewForm form = ReviewForm.builder()
            .rating(4.0)
            .text("test review")
            .build();

        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .status(ReservationStatus.CHECKED_IN)
            .userId(userId)
            .store(Store.builder().id(1L).reviewCount(10L).rating(4.0).build())
            .build();
        UserDto userDto = UserDto.builder()
            .name(userName)
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userClient.getInfo(token)).thenReturn(ResponseEntity.ok(userDto));
        // When
        String result = reviewService.create(userId, token, reservationId, form);

        // Then
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(storeRepository, times(1)).save(reservation.getStore());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(userClient, times(1)).getInfo(token);

        assertEquals("리뷰를 정상적으로 등록하였습니다.", result);
        assertEquals(11, reservation.getStore().getReviewCount());
        assertEquals(4.0, reservation.getStore().getRating());
    }

    @Test
    @DisplayName("리뷰 등록 실패-NotCheckInReservation")
    public void testRegistrationReview_NotCheckInReservation() {
        // Given
        Long userId = 1L;
        String token = "testToken";
        Long reservationId = 1L;
        ReviewForm form = ReviewForm.builder()
            .rating(4.0)
            .text("test review")
            .build();

        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .status(ReservationStatus.APPROVED)
            .store(Store.builder().id(1L).reviewCount(10L).rating(4.0).build())
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> reviewService.create(userId, token, reservationId, form));

        // Then
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(storeRepository, never()).save(reservation.getStore());
        verify(reviewRepository, never()).save(any(Review.class));

        assertEquals(NOT_CHECKIN_RESERVATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 등록 실패-DifferentUserId")
    public void testRegistrationReview_DifferentUserId() {
        // Given
        Long userId = 1L;
        Long otherUserId = 2L;
        String token = "testToken";
        Long reservationId = 1L;
        ReviewForm form = ReviewForm.builder()
            .rating(4.0)
            .text("test review")
            .build();

        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .userId(userId)
            .status(ReservationStatus.CHECKED_IN)
            .store(Store.builder().id(1L).reviewCount(10L).rating(4.0).build())
            .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> reviewService.create(otherUserId, token, reservationId, form));

        // Then
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(storeRepository, never()).save(reservation.getStore());
        verify(reviewRepository, never()).save(any(Review.class));

        assertEquals(DIFFERENT_USERID, exception.getErrorCode());
    }

    @Test
    @DisplayName("상점 리뷰 목록 조회- 성공")
    public void testGetReviews_Success() {
        // Given
        Long storeId = 1L;

        Store store = Store.builder()
            .id(storeId)
            .build();

        List<Review> expectedReviews = Arrays.asList(
            Review.builder().id(1L).build(),
            Review.builder().id(2L).build()
        );

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(reviewRepository.findByStoreOrderByModifiedAtDesc(store)).thenReturn(expectedReviews);

        // When
        List<Review> result = reviewService.getReviews(storeId);

        // Then
        verify(storeRepository, times(1)).findById(storeId);
        verify(reviewRepository, times(1)).findByStoreOrderByModifiedAtDesc(store);

        assertEquals(expectedReviews, result);
    }
}