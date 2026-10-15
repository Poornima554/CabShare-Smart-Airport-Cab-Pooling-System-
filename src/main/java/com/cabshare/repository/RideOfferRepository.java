package com.cabshare.repository;

import com.cabshare.model.Driver;
import com.cabshare.model.OfferStatus;
import com.cabshare.model.Ride;
import com.cabshare.model.RideOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RideOfferRepository extends JpaRepository<RideOffer, Long> {
    List<RideOffer> findByDriverAndStatusOrderByIdDesc(Driver driver, OfferStatus status);
    List<RideOffer> findByRideAndStatus(Ride ride, OfferStatus status);
    boolean existsByRideAndDriverAndStatus(Ride ride, Driver driver, OfferStatus status);
    boolean existsByRideAndDriverAndStatusAndExpiresAtAfter(Ride ride, Driver driver, OfferStatus status, LocalDateTime time);

    /** Only reads the ride id, so no Ride object is loaded before we lock the ride row. */
    @Query("select o.ride.id from RideOffer o where o.id = :offerId")
    Optional<Long> findRideIdByOfferId(@Param("offerId") Long offerId);
}
