package com.ridelink.ride.client;

/**
 * Mirrors the shape returned by Driver & Vehicle Service's
 * GET /api/drivers/available endpoint. Only the fields Ride
 * Management actually needs are mapped (stable identifier only -
 * no direct access to the Driver Service's database).
 */
public class AvailableDriverDto {

    private Long driverId;
    private String name;
    private String serviceArea;

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getServiceArea() { return serviceArea; }
    public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }
}
