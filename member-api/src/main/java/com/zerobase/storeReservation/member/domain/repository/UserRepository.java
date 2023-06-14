package com.zerobase.storeReservation.member.domain.repository;

import com.zerobase.storeReservation.member.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndEmail(Long id, String email);
    Optional<User> findByEmailAndPassword(String email, String password);
}
