package com.cabshare.repository;

import com.cabshare.model.TripRequest;
import com.cabshare.model.TripStatus;
import com.cabshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {
    List<TripRequest> findByPassengerOrderByIdDesc(User passenger);
    List<TripRequest> findByStatusOrderByIdAsc(TripStatus status);
    boolean existsByPassengerAndStatusIn(User passenger, Collection<TripStatus> statuses);
}
