package com.ridelink.drivervehicleservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Registration number is required")
    @Size(max = 20, message = "Registration number cannot exceed 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    private String registrationNumber;

    @NotBlank(message = "Vehicle make is required")
    @Size(max = 50, message = "Vehicle make cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String make;

    @NotBlank(message = "Vehicle model is required")
    @Size(max = 50, message = "Vehicle model cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String model;

    @NotBlank(message = "Vehicle colour is required")
    @Size(max = 30, message = "Vehicle colour cannot exceed 30 characters")
    @Column(nullable = false, length = 30)
    private String colour;

    @NotBlank(message = "Vehicle type is required")
    @Size(max = 30, message = "Vehicle type cannot exceed 30 characters")
    @Column(nullable = false, length = 30)
    private String vehicleType;

    @NotNull(message = "Manufacture year is required")
    @Min(value = 1980, message = "Manufacture year must be 1980 or later")
    @Max(value = 2100, message = "Manufacture year is invalid")
    @Column(nullable = false)
    private Integer manufactureYear;

    @NotNull(message = "Driver is required")
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "driver_id", nullable = false, unique = true)
    private Driver driver;

    public Vehicle() {
    }

    public Vehicle(String registrationNumber, String make, String model,
                   String colour, String vehicleType, Integer manufactureYear,
                   Driver driver) {
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.colour = colour;
        this.vehicleType = vehicleType;
        this.manufactureYear = manufactureYear;
        this.driver = driver;
    }

    public Long getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}