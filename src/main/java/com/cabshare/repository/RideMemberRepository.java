package com.cabshare.repository;

import com.cabshare.model.RideMember;
import com.cabshare.model.TripRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RideMemberRepository extends JpaRepository<RideMember, Long> {
    Optional<RideMember> findFirstByTripRequestOrderByIdDesc(TripRequest tripRequest);
}
