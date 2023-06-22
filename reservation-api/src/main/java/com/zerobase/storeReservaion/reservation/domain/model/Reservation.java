package com.zerobase.storeReservaion.reservation.domain.model;

import java.time.LocalDateTime;
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
public class Reservation extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime dateTime;

    private boolean approval;

    private boolean refuse;

    private boolean checkIn;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    public static Reservation of(Long userId, LocalDateTime requestDateTime, Store store) {
        return Reservation.builder()
            .userId(userId)
            .store(store)
            .dateTime(requestDateTime)
            .approval(false)
            .checkIn(false)
            .build();
    }
}
