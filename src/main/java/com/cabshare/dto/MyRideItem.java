package com.cabshare.dto;

import com.cabshare.model.*;

public class MyRideItem {

    private TripRequest request;

    private RideMember member;

    private RideGroup rideGroup;

    private Ride ride;

    private Payment payment;

    public TripRequest getRequest() { return request; }
    public void setRequest(TripRequest request) { this.request = request; }

    public RideMember getMember() { return member; }
    public void setMember(RideMember member) { this.member = member; }

    public RideGroup getRideGroup() { return rideGroup; }
    public void setRideGroup(RideGroup rideGroup) { this.rideGroup = rideGroup; }

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

}
