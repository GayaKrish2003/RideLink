package com.ridelink.ride.dto;

import com.ridelink.ride.entity.RideStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotNull(message = "status is required")
    private RideStatus status;

    // Optional - required only when status = CANCELLED
    private String reason;

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
