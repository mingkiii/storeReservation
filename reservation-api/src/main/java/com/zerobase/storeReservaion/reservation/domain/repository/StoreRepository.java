package com.zerobase.storeReservaion.reservation.domain.repository;

import com.zerobase.storeReservaion.reservation.domain.model.Store;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {
    Optional<Store> findByPartnerIdAndName(Long partnerId, String name);
}