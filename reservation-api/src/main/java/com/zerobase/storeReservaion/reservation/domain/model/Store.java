package com.zerobase.storeReservaion.reservation.domain.model;

import com.zerobase.storeReservaion.reservation.domain.form.AddStoreForm;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class Store extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long partnerId;

    @NotBlank(message = "필수 입력")
    private String name;

    @NotBlank(message = "필수 입력, 지번 주소로 입력해주세요.")
    private String address;

    @NotBlank(message = "필수 입력")
    private String text;

    @DecimalMin(value = "1.0", inclusive = true, message = "최소 1.0 점")
    @DecimalMax(value = "5.0", inclusive = true, message = "최대 5.0 점")
    @Digits(integer = 1, fraction = 1, message = "소수점은 0.5 단위로만 가능합니다.")
    @Positive(message = "양수값만 가능합니다.")
    private double rating;

    private Long reviewCount;

    private double latitude;
    private double longitude;

    public static Store of(Long partnerId, AddStoreForm form, double lat, double lon) {
        return Store.builder()
            .partnerId(partnerId)
            .name(form.getName())
            .address(form.getAddress())
            .text(form.getText())
            .latitude(lat)
            .longitude(lon)
            .rating(0.0)
            .reviewCount(0L)
            .build();
    }
}
