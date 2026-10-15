package com.cabshare.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ride_members")
public class RideMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private RideGroup rideGroup;

    @ManyToOne
    @JoinColumn(name = "trip_request_id", nullable = false)
    private TripRequest tripRequest;

    @Column(precision = 10, scale = 2)
    private BigDecimal fareShare;

    @Enumerated(EnumType.STRING)
    private AcceptanceStatus acceptance;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RideGroup getRideGroup() { return rideGroup; }
    public void setRideGroup(RideGroup rideGroup) { this.rideGroup = rideGroup; }

    public TripRequest getTripRequest() { return tripRequest; }
    public void setTripRequest(TripRequest tripRequest) { this.tripRequest = tripRequest; }

    public BigDecimal getFareShare() { return fareShare; }
    public void setFareShare(BigDecimal fareShare) { this.fareShare = fareShare; }

    public AcceptanceStatus getAcceptance() { return acceptance; }
    public void setAcceptance(AcceptanceStatus acceptance) { this.acceptance = acceptance; }

}
