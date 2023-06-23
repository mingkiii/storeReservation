package com.zerobase.storeReservaion.reservation.domain.model;

import com.zerobase.storeReservaion.reservation.domain.form.ReviewForm;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Review extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String userName;

    @DecimalMin(value = "1.0", inclusive = true, message = "최소 1.0 점")
    @DecimalMax(value = "5.0", inclusive = true, message = "최대 5.0 점")
    @Digits(integer = 1, fraction = 1, message = "소수점은 0.5 단위로만 가능합니다.")
    @Positive(message = "양수값만 가능합니다.")
    @NotBlank(message = "필수 입력")
    private double rating;

    @NotBlank(message = "필수 입력")
    private String text;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    public static Review of(Long userId, String userName, ReviewForm form, Store store) {
        return Review.builder()
            .userId(userId)
            .userName(userName)
            .rating(form.getRating())
            .text(form.getText())
            .store(store)
            .build();
    }
}
