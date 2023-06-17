package com.zerobase.storeReservaion.reservation.domain.model;

import com.zerobase.storeReservaion.reservation.domain.form.AddStoreForm;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

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
