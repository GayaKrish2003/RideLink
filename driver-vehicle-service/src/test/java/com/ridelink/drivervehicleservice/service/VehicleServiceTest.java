package com.ridelink.drivervehicleservice.service;

import com.ridelink.drivervehicleservice.dto.VehicleRequest;
import com.ridelink.drivervehicleservice.entity.Driver;
import com.ridelink.drivervehicleservice.entity.Vehicle;
import com.ridelink.drivervehicleservice.exception.ResourceNotFoundException;
import com.ridelink.drivervehicleservice.repository.DriverRepository;
import com.ridelink.drivervehicleservice.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRequest request;

    @Mock
    private Driver driver;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void createVehicle_success() {

        stubValidRequest();

        when(vehicleRepository.findByRegistrationNumber("CAB-1234"))
                .thenReturn(Optional.empty());

        when(driverRepository.findById(2L))
                .thenReturn(Optional.of(driver));

        when(vehicleRepository.findByDriverId(2L))
                .thenReturn(Optional.empty());

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle result = vehicleService.createVehicle(request);

        assertNotNull(result);
        assertEquals("CAB-1234", result.getRegistrationNumber());
        assertEquals("Toyota", result.getMake());
        assertEquals("Aqua", result.getModel());
        assertEquals("White", result.getColour());
        assertEquals("Car", result.getVehicleType());
        assertEquals(2020, result.getManufactureYear());
        assertSame(driver, result.getDriver());

        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void createVehicle_duplicateRegistration_throwsException() {

        when(request.getRegistrationNumber()).thenReturn("CAB-1234");

        when(vehicleRepository.findByRegistrationNumber("CAB-1234"))
                .thenReturn(Optional.of(mock(Vehicle.class)));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleService.createVehicle(request)
        );

        assertEquals(
                "Registration number already exists",
                exception.getMessage()
        );

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void getAllVehicles_returnsVehicleList() {

        Vehicle vehicle = mock(Vehicle.class);

        when(vehicleRepository.findAll())
                .thenReturn(List.of(vehicle));

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertEquals(1, result.size());
        assertSame(vehicle, result.get(0));
    }

    @Test
    void getVehicleById_whenVehicleExists_returnsVehicle() {

        Vehicle vehicle = mock(Vehicle.class);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.getVehicleById(1L);

        assertSame(vehicle, result);
    }

    @Test
    void getVehicleById_whenVehicleDoesNotExist_throwsException() {

        when(vehicleRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> vehicleService.getVehicleById(999L)
        );

        assertEquals(
                "Vehicle not found with ID: 999",
                exception.getMessage()
        );
    }

    @Test
    void deleteVehicle_whenVehicleExists_deletesVehicle() {

        Vehicle vehicle = mock(Vehicle.class);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository).delete(vehicle);
    }

    private void stubValidRequest() {

        when(request.getRegistrationNumber()).thenReturn("CAB-1234");
        when(request.getMake()).thenReturn("Toyota");
        when(request.getModel()).thenReturn("Aqua");
        when(request.getColour()).thenReturn("White");
        when(request.getVehicleType()).thenReturn("Car");
        when(request.getManufactureYear()).thenReturn(2020);
        when(request.getDriverId()).thenReturn(2L);
    }
}