package com.cabshare.model;

/**
 * Life cycle of a ride. A ride can only move forward one step at a time:
 * DRIVER_ASSIGNED -> DRIVER_ARRIVING -> AT_PICKUP -> IN_PROGRESS -> COMPLETED
 */
public enum RideStatus {
    AWAITING_DRIVER,
    DRIVER_ASSIGNED,
    DRIVER_ARRIVING,
    AT_PICKUP,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    /** The only status the driver is allowed to move to from this status (null if none). */
    public RideStatus next() {
        switch (this) {
            case DRIVER_ASSIGNED: return DRIVER_ARRIVING;
            case DRIVER_ARRIVING: return AT_PICKUP;
            case AT_PICKUP:       return IN_PROGRESS;
            case IN_PROGRESS:     return COMPLETED;
            default:              return null;
        }
    }

    public boolean canMoveTo(RideStatus target) {
        return target != null && next() == target;
    }
}
