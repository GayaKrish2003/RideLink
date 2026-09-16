package com.ridelink.farepaymentservice.dto;

import java.time.LocalDateTime;

public class FareEstimateResponse {

    private Long id;
    private String pickupLocation;
    private String destination;
    private double distanceKm;
    private double estimatedFare;
    private LocalDateTime createdAt;

    public FareEstimateResponse(Long id, String pickupLocation, String destination,
                                double distanceKm, double estimatedFare, LocalDateTime createdAt) {
        this.id = id;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedFare = estimatedFare;
        this.createdAt = createdAt;
    }

    // Getters only needed for response (Jackson uses these to build JSON)
    public Long getId() { return id; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDestination() { return destination; }
    public double getDistanceKm() { return distanceKm; }
    public double getEstimatedFare() { return estimatedFare; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}