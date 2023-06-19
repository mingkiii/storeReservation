package com.zerobase.storeReservaion.reservation.service;

import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_CHECKIN_RESERVATION;
import static com.zerobase.storeReservaion.reservation.exception.ErrorCode.NOT_FOUND_RESERVATION;

import com.zerobase.storeReservaion.reservation.domain.form.ReviewForm;
import com.zerobase.storeReservaion.reservation.domain.model.Reservation;
import com.zerobase.storeReservaion.reservation.domain.model.Review;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
import com.zerobase.storeReservaion.reservation.domain.repository.ReservationRepository;
import com.zerobase.storeReservaion.reservation.domain.repository.ReviewRepository;
import com.zerobase.storeReservaion.reservation.domain.repository.StoreRepository;
import com.zerobase.storeReservaion.reservation.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    public String registrationReview(Long userId, Long reservationId, ReviewForm form) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_RESERVATION));
        if (!reservation.isCheckIn()) {
            throw new CustomException(NOT_CHECKIN_RESERVATION);
        }

        Store store = reservation.getStore();
        double newRating = (store.getRating() * store.getReviewCount() + form.getRating()) / (store.getReviewCount() + 1);
        store.setReviewCount(store.getReviewCount() + 1);
        store.setRating(Math.round(newRating * 10) / 10.0);
        storeRepository.save(store);
        reviewRepository.save(Review.of(userId, form, reservation.getStore()));

        return "리뷰를 정상적으로 등록하였습니다.";
    }
}
