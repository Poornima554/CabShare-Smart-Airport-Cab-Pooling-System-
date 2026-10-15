package com.cabshare.dto;

import com.cabshare.model.*;
import java.util.List;

public class RideRow {

    private Ride ride;

    private List<RideStatusHistory> history;

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }

    public List<RideStatusHistory> getHistory() { return history; }
    public void setHistory(List<RideStatusHistory> history) { this.history = history; }

}
