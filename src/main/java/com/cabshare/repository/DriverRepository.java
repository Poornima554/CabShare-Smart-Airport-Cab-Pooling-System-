package com.cabshare.repository;

import com.cabshare.model.Driver;
import com.cabshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByUser(User user);
    List<Driver> findByVerifiedTrueAndOnlineTrueAndAvailableTrue();
}
