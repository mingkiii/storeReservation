package com.zerobase.storeReservaion.reservation.domain.repository;

import com.zerobase.storeReservaion.reservation.domain.model.Reservation;
import com.zerobase.storeReservaion.reservation.domain.model.Store;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 경계 포함하지 않음.
    long countByDateTimeGreaterThanAndDateTimeLessThanAndStore(
        LocalDateTime startDateTime, LocalDateTime endDateTime, Store store);

    List<Reservation> findByStoreOrderByDateTime(Store store);
}
