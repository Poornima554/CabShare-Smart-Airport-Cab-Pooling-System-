package com.cabshare.repository;

import com.cabshare.model.Payment;
import com.cabshare.model.Ride;
import com.cabshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRideAndUser(Ride ride, User user);
}
