package com.ridelink.drivervehicleservice.repository;

import com.ridelink.drivervehicleservice.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByAccountId(Long accountId);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    List<Driver> findByAvailableTrue();

    List<Driver> findByAvailableTrueAndServiceAreaIgnoreCase(String serviceArea);
}