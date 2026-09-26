package com.ridelink.drivervehicleservice.service;

import com.ridelink.drivervehicleservice.entity.Driver;
import com.ridelink.drivervehicleservice.exception.ResourceNotFoundException;
import com.ridelink.drivervehicleservice.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    @Test
    void createDriver_success() {

        Driver driver = mock(Driver.class);

        when(driver.getAccountId()).thenReturn(1001L);
        when(driver.getLicenseNumber()).thenReturn("B1234567");

        when(driverRepository.findByAccountId(1001L))
                .thenReturn(Optional.empty());

        when(driverRepository.findByLicenseNumber("B1234567"))
                .thenReturn(Optional.empty());

        when(driverRepository.save(driver)).thenReturn(driver);

        Driver result = driverService.createDriver(driver);

        assertSame(driver, result);
        verify(driverRepository).save(driver);
    }

    @Test
    void createDriver_duplicateAccount_throwsException() {

        Driver driver = mock(Driver.class);

        when(driver.getAccountId()).thenReturn(1001L);

        when(driverRepository.findByAccountId(1001L))
                .thenReturn(Optional.of(mock(Driver.class)));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> driverService.createDriver(driver)
        );

        assertEquals(
                "A driver already exists for this account",
                exception.getMessage()
        );

        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void createDriver_duplicateLicenseNumber_throwsException() {

        Driver driver = mock(Driver.class);

        when(driver.getAccountId()).thenReturn(1001L);
        when(driver.getLicenseNumber()).thenReturn("B1234567");

        when(driverRepository.findByAccountId(1001L))
                .thenReturn(Optional.empty());

        when(driverRepository.findByLicenseNumber("B1234567"))
                .thenReturn(Optional.of(mock(Driver.class)));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> driverService.createDriver(driver)
        );

        assertEquals(
                "License number already exists",
                exception.getMessage()
        );

        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void getAllDrivers_returnsDriverList() {

        Driver driver = mock(Driver.class);

        when(driverRepository.findAll())
                .thenReturn(List.of(driver));

        List<Driver> result = driverService.getAllDrivers();

        assertEquals(1, result.size());
        assertSame(driver, result.get(0));
    }

    @Test
    void getDriverById_whenDriverExists_returnsDriver() {

        Driver driver = mock(Driver.class);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        Driver result = driverService.getDriverById(1L);

        assertSame(driver, result);
    }

    @Test
    void getDriverById_whenDriverDoesNotExist_throwsException() {

        when(driverRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> driverService.getDriverById(999L)
        );
    }

    @Test
    void updateAvailability_success() {

        Driver driver = mock(Driver.class);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        when(driverRepository.save(driver)).thenReturn(driver);

        Driver result = driverService.updateAvailability(1L, false);

        assertSame(driver, result);
        verify(driver).setAvailable(false);
        verify(driverRepository).save(driver);
    }

    @Test
    void deleteDriver_whenDriverExists_deletesDriver() {

        Driver driver = mock(Driver.class);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        driverService.deleteDriver(1L);

        verify(driverRepository).delete(driver);
    }
    @Test
    void getEligibleDrivers_withoutServiceArea_returnsAllAvailableDrivers() {

        Driver driver = mock(Driver.class);

        when(driverRepository.findByAvailableTrue())
                .thenReturn(List.of(driver));

        List<Driver> result = driverService.getEligibleDrivers(null);

        assertEquals(1, result.size());
        assertSame(driver, result.get(0));

        verify(driverRepository).findByAvailableTrue();
        verify(driverRepository, never())
                .findByAvailableTrueAndServiceAreaIgnoreCase(anyString());
    }

    @Test
    void getEligibleDrivers_withServiceArea_returnsAvailableDriversInArea() {

        Driver driver = mock(Driver.class);

        when(driverRepository
                .findByAvailableTrueAndServiceAreaIgnoreCase("Colombo"))
                .thenReturn(List.of(driver));

        List<Driver> result =
                driverService.getEligibleDrivers(" Colombo ");

        assertEquals(1, result.size());
        assertSame(driver, result.get(0));

        verify(driverRepository)
                .findByAvailableTrueAndServiceAreaIgnoreCase("Colombo");
        verify(driverRepository, never()).findByAvailableTrue();
    }
}