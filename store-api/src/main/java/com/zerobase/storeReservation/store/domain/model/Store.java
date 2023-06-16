package com.zerobase.storeReservation.store.domain.model;

import com.zerobase.storeReservation.store.domain.form.AddStoreForm;
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

    private Long partnerId;

    private String name;

    private String address;

    private String text;

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
