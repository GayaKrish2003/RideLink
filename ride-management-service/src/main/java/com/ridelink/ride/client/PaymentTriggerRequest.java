package com.ridelink.ride.client;

/**
 * Body sent to Fare & Payment Service's POST /api/payments endpoint
 * when a ride's status changes to COMPLETED (Interaction 2, sync REST).
 */
public class PaymentTriggerRequest {

    private Long rideId;
    private Double distanceKm;
    private Integer durationMin;

    public PaymentTriggerRequest() {}

    public PaymentTriggerRequest(Long rideId, Double distanceKm, Integer durationMin) {
        this.rideId = rideId;
        this.distanceKm = distanceKm;
        this.durationMin = durationMin;
    }

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getDurationMin() { return durationMin; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }
}
