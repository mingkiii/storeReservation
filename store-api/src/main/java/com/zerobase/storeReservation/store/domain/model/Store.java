package com.zerobase.storeReservation.store.domain.model;

import com.zerobase.storeReservation.store.domain.AddStoreForm;
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

    public static Store of(Long partnerId, AddStoreForm form) {
        return Store.builder()
            .partnerId(partnerId)
            .name(form.getName())
            .address(form.getAddress())
            .text(form.getText())
            .build();
    }
}
