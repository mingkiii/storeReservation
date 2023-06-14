package com.zerobase.storeReservation.member.domain.repository;

import com.zerobase.storeReservation.member.domain.model.Partner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    Optional<Partner> findByEmail(String email);
    Optional<Partner> findByIdAndEmail(Long id, String email);
    Optional<Partner> findByEmailAndPassword(String email, String password);
}
