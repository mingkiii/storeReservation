package com.zerobase.storeReservaion.reservation.domain.model;

import com.zerobase.storeReservaion.reservation.domain.form.AddStoreForm;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    @Column
    private Long partnerId;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private String text;

    @Column
    private double rating;

    @Column
    private Long reviewCount;

    @Column
    private double latitude;
    @Column
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
