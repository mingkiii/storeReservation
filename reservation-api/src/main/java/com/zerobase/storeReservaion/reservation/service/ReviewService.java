package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.DIFFERENT_USERID;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_CHECKIN_RESERVATION;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_FOUND_RESERVATION;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_FOUND_STORE;

import com.zerobase.storeReservaion.reservation.client.UserClient;
import com.zerobase.storeReservaion.reservation.client.UserDto;
import com.zerobase.storeReservaion.reservation.domain.form.ReviewForm;
import com.zerobase.storeReservaion.reservation.domain.model.Reservation;
import com.zerobase.storeReservaion.reservation.domain.model.Review;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
import com.zerobase.storeReservaion.reservation.domain.repository.ReservationRepository;
import com.zerobase.storeReservaion.reservation.domain.repository.ReviewRepository;
import com.zerobase.storeReservaion.reservation.domain.repository.StoreRepository;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    private final UserClient userClient;

    public String create(Long userId, String token, Long reservationId,
        ReviewForm form) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_RESERVATION));
        if (!reservation.isCheckIn()) {
            throw new CustomException(NOT_CHECKIN_RESERVATION);
        }
        if (!Objects.equals(userId, reservation.getUserId())) {
            throw new CustomException(DIFFERENT_USERID);
        }

        UserDto userDto = userClient.getInfo(token).getBody(); // UserClient를 사용하여 UserDto를 가져옴
        String userName = Objects.requireNonNull(userDto).getName(); // UserDto에서 userName 추출

        Store store = reservation.getStore();
        double newRating =
            (store.getRating() * store.getReviewCount() + form.getRating()) /
                (store.getReviewCount() + 1);
        store.setReviewCount(store.getReviewCount() + 1);
        store.setRating(Math.round(newRating * 10) / 10.0);

        storeRepository.save(store);
        reviewRepository.save(Review.of(userId, userName, form, reservation.getStore()));

        return "리뷰를 정상적으로 등록하였습니다.";
    }

    public List<Review> getReviews(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_STORE));
        return reviewRepository.findByStoreOrderByModifiedAtDesc(store);
    }
}
