package com.ridelink.ride.service;

import com.ridelink.ride.client.AvailableDriverDto;
import com.ridelink.ride.client.DriverServiceClient;
import com.ridelink.ride.client.FareServiceClient;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.UpdateStatusRequest;
import com.ridelink.ride.entity.Ride;
import com.ridelink.ride.entity.RideStatus;
import com.ridelink.ride.exception.InvalidStatusTransitionException;
import com.ridelink.ride.exception.NoAvailableDriverException;
import com.ridelink.ride.exception.RideNotFoundException;
import com.ridelink.ride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RideServiceTest {

    @Mock private RideRepository rideRepository;
    @Mock private DriverServiceClient driverServiceClient;
    @Mock private FareServiceClient fareServiceClient;

    private RideService rideService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rideService = new RideService(rideRepository, driverServiceClient, new RideStateMachine(), fareServiceClient);
    }

    @Test
    void createRideAssignsDriverWhenOneIsAvailable() {
        CreateRideRequest request = new CreateRideRequest();
        request.setPassengerId(1L);
        request.setPickupLocation("Negombo");
        request.setDestinationLocation("Colombo");

        AvailableDriverDto driver = new AvailableDriverDto();
        driver.setDriverId(42L);
        when(driverServiceClient.findFirstAvailableDriver(any())).thenReturn(Optional.of(driver));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        Ride ride = rideService.createRide(request, "Bearer token");

        assertEquals(42L, ride.getDriverId());
        assertEquals(RideStatus.ASSIGNED, ride.getStatus());
    }

    @Test
    void createRideThrowsWhenNoDriverAvailable() {
        CreateRideRequest request = new CreateRideRequest();
        request.setPassengerId(1L);
        request.setPickupLocation("Negombo");
        request.setDestinationLocation("Colombo");

        when(driverServiceClient.findFirstAvailableDriver(any())).thenReturn(Optional.empty());

        assertThrows(NoAvailableDriverException.class, () -> rideService.createRide(request, "Bearer token"));
        verify(rideRepository, never()).save(any());
    }

    @Test
    void getByIdThrowsWhenRideMissing() {
        when(rideRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RideNotFoundException.class, () -> rideService.getById(99L));
    }

    @Test
    void updateStatusRejectsInvalidTransition() {
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setStatus(RideStatus.REQUESTED);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(RideStatus.COMPLETED);

        assertThrows(InvalidStatusTransitionException.class,
                () -> rideService.updateStatus(1L, request, "Bearer token"));
        verify(fareServiceClient, never()).triggerPayment(any(), any());
    }

    @Test
    void updateStatusToCompletedTriggersPayment() {
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setStatus(RideStatus.IN_PROGRESS);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(RideStatus.COMPLETED);

        Ride result = rideService.updateStatus(1L, request, "Bearer token");

        assertEquals(RideStatus.COMPLETED, result.getStatus());
        verify(fareServiceClient, times(1)).triggerPayment(any(), eq("Bearer token"));
    }

    @Test
    void cancellingRideRecordsReason() {
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setStatus(RideStatus.ASSIGNED);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(RideStatus.CANCELLED);
        request.setReason("Passenger changed plans");

        Ride result = rideService.updateStatus(1L, request, "Bearer token");

        assertEquals(RideStatus.CANCELLED, result.getStatus());
        assertEquals("Passenger changed plans", result.getCancellationReason());
    }
}
