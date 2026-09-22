package com.ridelink.drivervehicleservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Account ID is required")
    @Column(nullable = false, unique = true)
    private Long accountId;

    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number cannot exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @NotBlank(message = "Service area is required")
    @Size(max = 100, message = "Service area cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String serviceArea;

    @Size(max = 150, message = "Current location cannot exceed 150 characters")
    @Column(length = 150)
    private String currentLocation;

    @Column(nullable = false)
    private boolean available = false;

    public Driver() {
    }

    public Driver(Long accountId, String licenseNumber, String serviceArea,
                  String currentLocation, boolean available) {
        this.accountId = accountId;
        this.licenseNumber = licenseNumber;
        this.serviceArea = serviceArea;
        this.currentLocation = currentLocation;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}