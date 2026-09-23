package com.ridelink.drivervehicleservice.service;

import com.ridelink.drivervehicleservice.dto.VehicleRequest;
import com.ridelink.drivervehicleservice.entity.Driver;
import com.ridelink.drivervehicleservice.entity.Vehicle;
import com.ridelink.drivervehicleservice.exception.ResourceNotFoundException;
import com.ridelink.drivervehicleservice.repository.DriverRepository;
import com.ridelink.drivervehicleservice.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    public Vehicle createVehicle(VehicleRequest request) {
        vehicleRepository
                .findByRegistrationNumber(request.getRegistrationNumber())
                .ifPresent(existingVehicle -> {
                    throw new IllegalArgumentException(
                            "Registration number already exists"
                    );
                });

        Driver driver = getDriverById(request.getDriverId());

        vehicleRepository
                .findByDriverId(request.getDriverId())
                .ifPresent(existingVehicle -> {
                    throw new IllegalArgumentException(
                            "This driver already has a vehicle"
                    );
                });

        Vehicle vehicle = new Vehicle(
                request.getRegistrationNumber(),
                request.getMake(),
                request.getModel(),
                request.getColour(),
                request.getVehicleType(),
                request.getManufactureYear(),
                driver
        );

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found with ID: " + id
                ));
    }

    public Vehicle getVehicleByDriverId(Long driverId) {
        return vehicleRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found for driver ID: " + driverId
                ));
    }

    public Vehicle updateVehicle(Long id, VehicleRequest request) {
        Vehicle existingVehicle = getVehicleById(id);

        vehicleRepository
                .findByRegistrationNumber(request.getRegistrationNumber())
                .filter(vehicle -> !vehicle.getId().equals(id))
                .ifPresent(vehicle -> {
                    throw new IllegalArgumentException(
                            "Registration number already exists"
                    );
                });

        Driver driver = getDriverById(request.getDriverId());

        vehicleRepository
                .findByDriverId(request.getDriverId())
                .filter(vehicle -> !vehicle.getId().equals(id))
                .ifPresent(vehicle -> {
                    throw new IllegalArgumentException(
                            "This driver already has a vehicle"
                    );
                });

        existingVehicle.setRegistrationNumber(
                request.getRegistrationNumber()
        );
        existingVehicle.setMake(request.getMake());
        existingVehicle.setModel(request.getModel());
        existingVehicle.setColour(request.getColour());
        existingVehicle.setVehicleType(request.getVehicleType());
        existingVehicle.setManufactureYear(
                request.getManufactureYear()
        );
        existingVehicle.setDriver(driver);

        return vehicleRepository.save(existingVehicle);
    }

    public void deleteVehicle(Long id) {
        Vehicle vehicle = getVehicleById(id);
        vehicleRepository.delete(vehicle);
    }

    private Driver getDriverById(Long driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found with ID: " + driverId
                ));
    }
}