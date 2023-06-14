package com.zerobase.storeReservation.store.domain.repository;

import com.zerobase.storeReservation.store.domain.model.Store;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByPartnerIdAndName(Long partnerId, String name);
}
