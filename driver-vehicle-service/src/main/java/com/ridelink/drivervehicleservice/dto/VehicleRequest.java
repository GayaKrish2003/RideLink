package com.ridelink.drivervehicleservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VehicleRequest {

    @NotBlank(message = "Registration number is required")
    @Size(max = 20, message = "Registration number cannot exceed 20 characters")
    private String registrationNumber;

    @NotBlank(message = "Vehicle make is required")
    @Size(max = 50, message = "Vehicle make cannot exceed 50 characters")
    private String make;

    @NotBlank(message = "Vehicle model is required")
    @Size(max = 50, message = "Vehicle model cannot exceed 50 characters")
    private String model;

    @NotBlank(message = "Vehicle colour is required")
    @Size(max = 30, message = "Vehicle colour cannot exceed 30 characters")
    private String colour;

    @NotBlank(message = "Vehicle type is required")
    @Size(max = 30, message = "Vehicle type cannot exceed 30 characters")
    private String vehicleType;

    @NotNull(message = "Manufacture year is required")
    @Min(value = 1980, message = "Manufacture year must be 1980 or later")
    @Max(value = 2100, message = "Manufacture year is invalid")
    private Integer manufactureYear;

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getColour() {
        return colour;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public Long getDriverId() {
        return driverId;
    }
}