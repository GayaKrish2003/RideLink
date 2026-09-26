package com.ridelink.ride.dto;

import com.ridelink.ride.entity.Ride;
import com.ridelink.ride.entity.RideStatus;

import java.time.Instant;

public class RideResponse {

    private Long id;
    private Long passengerId;
    private Long driverId;
    private String pickupLocation;
    private String destinationLocation;
    private Double distanceKm;
    private Integer durationMin;
    private RideStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String cancellationReason;

    public static RideResponse from(Ride ride) {
        RideResponse r = new RideResponse();
        r.id = ride.getId();
        r.passengerId = ride.getPassengerId();
        r.driverId = ride.getDriverId();
        r.pickupLocation = ride.getPickupLocation();
        r.destinationLocation = ride.getDestinationLocation();
        r.distanceKm = ride.getDistanceKm();
        r.durationMin = ride.getDurationMin();
        r.status = ride.getStatus();
        r.createdAt = ride.getCreatedAt();
        r.updatedAt = ride.getUpdatedAt();
        r.cancellationReason = ride.getCancellationReason();
        return r;
    }

    public Long getId() { return id; }
    public Long getPassengerId() { return passengerId; }
    public Long getDriverId() { return driverId; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDestinationLocation() { return destinationLocation; }
    public Double getDistanceKm() { return distanceKm; }
    public Integer getDurationMin() { return durationMin; }
    public RideStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCancellationReason() { return cancellationReason; }
}
