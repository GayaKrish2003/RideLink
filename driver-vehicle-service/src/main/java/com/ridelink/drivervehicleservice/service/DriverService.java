package com.ridelink.drivervehicleservice.service;

import com.ridelink.drivervehicleservice.entity.Driver;
import com.ridelink.drivervehicleservice.repository.DriverRepository;
import org.springframework.stereotype.Service;
import com.ridelink.drivervehicleservice.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver createDriver(Driver driver) {
        if (driverRepository.findByAccountId(driver.getAccountId()).isPresent()) {
            throw new IllegalArgumentException("A driver already exists for this account");
        }

        if (driverRepository.findByLicenseNumber(driver.getLicenseNumber()).isPresent()) {
            throw new IllegalArgumentException("License number already exists");
        }

        return driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found with ID: " + id));
    }

    public Driver updateDriver(Long id, Driver updatedDriver) {
        Driver existingDriver = getDriverById(id);

        driverRepository.findByAccountId(updatedDriver.getAccountId())
                .filter(driver -> !driver.getId().equals(id))
                .ifPresent(driver -> {
                    throw new IllegalArgumentException(
                            "A driver already exists for this account");
                });

        driverRepository.findByLicenseNumber(updatedDriver.getLicenseNumber())
                .filter(driver -> !driver.getId().equals(id))
                .ifPresent(driver -> {
                    throw new IllegalArgumentException(
                            "License number already exists");
                });

        existingDriver.setAccountId(updatedDriver.getAccountId());
        existingDriver.setLicenseNumber(updatedDriver.getLicenseNumber());
        existingDriver.setServiceArea(updatedDriver.getServiceArea());
        existingDriver.setCurrentLocation(updatedDriver.getCurrentLocation());
        existingDriver.setAvailable(updatedDriver.isAvailable());

        return driverRepository.save(existingDriver);
    }

    public Driver updateAvailability(Long id, boolean available) {
        Driver driver = getDriverById(id);
        driver.setAvailable(available);
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        Driver driver = getDriverById(id);
        driverRepository.delete(driver);
    }
}