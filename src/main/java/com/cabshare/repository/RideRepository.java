package com.cabshare.repository;

import com.cabshare.model.Driver;
import com.cabshare.model.Ride;
import com.cabshare.model.RideGroup;
import com.cabshare.model.RideStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {

    /** Locks the ride row until the transaction ends, so two drivers cannot take the same ride. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Ride r where r.id = :id")
    Optional<Ride> findByIdForUpdate(@Param("id") Long id);

    Optional<Ride> findByRideGroup(RideGroup rideGroup);
    List<Ride> findByStatusOrderByIdAsc(RideStatus status);
    List<Ride> findAllByOrderByIdDesc();
    List<Ride> findByDriverAndStatusIn(Driver driver, Collection<RideStatus> statuses);
    List<Ride> findByDriverAndStatusOrderByIdDesc(Driver driver, RideStatus status);
    long countByStatus(RideStatus status);
    long countByStatusIn(Collection<RideStatus> statuses);
}
