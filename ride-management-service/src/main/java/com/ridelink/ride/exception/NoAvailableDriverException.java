package com.ridelink.ride.exception;

public class NoAvailableDriverException extends RuntimeException {
    public NoAvailableDriverException() {
        super("No available driver found for this ride request");
    }
}
