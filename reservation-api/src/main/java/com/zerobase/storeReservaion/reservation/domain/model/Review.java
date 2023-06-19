package com.zerobase.storeReservaion.reservation.domain.model;

import com.zerobase.storeReservaion.reservation.domain.form.ReviewForm;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    private double rating;

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
