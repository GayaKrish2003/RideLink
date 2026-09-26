package com.ridelink.ride.exception;

import com.ridelink.ride.entity.RideStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(RideStatus from, RideStatus to) {
        super("Invalid ride status transition: " + from + " -> " + to);
    }
}
