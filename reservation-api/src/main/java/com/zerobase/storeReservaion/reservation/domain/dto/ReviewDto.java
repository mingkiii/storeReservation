package com.zerobase.storeReservaion.reservation.domain.dto;

import com.zerobase.storeReservaion.reservation.domain.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private String userName;
    private double rating;
    private String text;

    public static ReviewDto from(Review review) {
        return ReviewDto.builder()
            .userName(review.getUserName())
            .rating(review.getRating())
            .text(review.getText())
            .build();
    }
}
