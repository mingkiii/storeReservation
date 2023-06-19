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

    // 유저 - 리뷰 작성

    /**
     * @param userId        토큰으로 받은 user_id
     * @param token         userClient 사용을 위해 token 도 받음
     * @param reservationId 예약의 id, 예약을 통해 리뷰 작성 가능하도록
     * @param form          리뷰 작성 폼
     */
    public String create(Long userId, String token, Long reservationId,
        ReviewForm form) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_RESERVATION));
        // 예약이 이용되지 않은 경우(상점 방문 안한 경우)
        if (!reservation.isCheckIn()) {
            throw new CustomException(NOT_CHECKIN_RESERVATION);
        }
        // 유저가 가진 예약이 아닌 경우
        if (!Objects.equals(userId, reservation.getUserId())) {
            throw new CustomException(DIFFERENT_USERID);
        }

        // UserClient 를 사용하여 UserDto 를 가져옴
        UserDto userDto = userClient.getInfo(token).getBody();
        // UserDto 에서 userName 추출
        String userName = Objects.requireNonNull(userDto).getName();

        Store store = reservation.getStore();
        // 상점의 기존 별점 가져와서 별점 평균 계산하여 상점 별점, 리뷰 수 리셋
        double newRating =
            (store.getRating() * store.getReviewCount() + form.getRating()) /
                (store.getReviewCount() + 1);
        store.setReviewCount(store.getReviewCount() + 1);
        store.setRating(Math.round(newRating * 10) / 10.0);

        storeRepository.save(store);
        reviewRepository.save(Review.of(userId, userName, form, reservation.getStore()));

        return "리뷰를 정상적으로 등록하였습니다.";
    }

    // 상점의 리뷰 목록 가져오기
    public List<Review> getReviews(Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_STORE));
        return reviewRepository.findByStoreOrderByModifiedAtDesc(store);
    }
}
