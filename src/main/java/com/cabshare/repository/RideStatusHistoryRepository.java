package com.cabshare.repository;

import com.cabshare.model.Ride;
import com.cabshare.model.RideStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideStatusHistoryRepository extends JpaRepository<RideStatusHistory, Long> {
    List<RideStatusHistory> findByRideOrderByIdAsc(Ride ride);
}
