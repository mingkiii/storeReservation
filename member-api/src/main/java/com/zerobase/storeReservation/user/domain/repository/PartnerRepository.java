package com.zerobase.storeReservation.user.domain.repository;

import com.zerobase.storeReservation.user.domain.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    boolean existsByEmail(String email);
}
