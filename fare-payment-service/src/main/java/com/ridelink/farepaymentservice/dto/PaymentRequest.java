package com.ridelink.farepaymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequest {

    @NotNull(message = "Ride ID is required")
    private Long rideId;

    @Positive(message = "Distance must be greater than zero")
    private double distanceKm;

    @Positive(message = "Duration must be greater than zero")
    private double durationMin;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    // Getters and setters
    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public double getDurationMin() { return durationMin; }
    public void setDurationMin(double durationMin) { this.durationMin = durationMin; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}