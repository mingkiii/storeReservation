package com.zerobase.storeReservaion.reservation.domain.repository;

import com.zerobase.storeReservaion.reservation.domain.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

}
