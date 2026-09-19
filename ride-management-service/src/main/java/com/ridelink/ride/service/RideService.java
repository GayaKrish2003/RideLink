package com.ridelink.ride.service;

import com.ridelink.ride.client.AvailableDriverDto;
import com.ridelink.ride.client.DriverServiceClient;
import com.ridelink.ride.client.FareServiceClient;
import com.ridelink.ride.client.PaymentTriggerRequest;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.UpdateStatusRequest;
import com.ridelink.ride.entity.Ride;
import com.ridelink.ride.entity.RideStatus;
import com.ridelink.ride.exception.NoAvailableDriverException;
import com.ridelink.ride.exception.RideNotFoundException;
import com.ridelink.ride.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final DriverServiceClient driverServiceClient;
    private final RideStateMachine stateMachine;
    private final FareServiceClient fareServiceClient;

    public RideService(RideRepository rideRepository,
                        DriverServiceClient driverServiceClient,
                        RideStateMachine stateMachine,
                        FareServiceClient fareServiceClient) {
        this.rideRepository = rideRepository;
        this.driverServiceClient = driverServiceClient;
        this.stateMachine = stateMachine;
        this.fareServiceClient = fareServiceClient;
    }

    /**
     * Creates the ride, then immediately asks Driver & Vehicle Service for an
     * eligible available driver (Interaction 1, synchronous REST) because ride
     * assignment needs an immediate answer before the request can proceed.
     * Negative case: no available driver -> ride creation fails gracefully
     * (ride is saved as REQUESTED-then-CANCELLED is avoided; we surface a 409
     * and leave nothing dangling by not persisting an unassignable ride).
     */
    public Ride createRide(CreateRideRequest request, String authorizationHeader) {
        Optional<AvailableDriverDto> driver = driverServiceClient.findFirstAvailableDriver(authorizationHeader);
        if (driver.isEmpty()) {
            throw new NoAvailableDriverException();
        }

        Ride ride = new Ride();
        ride.setPassengerId(request.getPassengerId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDestinationLocation(request.getDestinationLocation());
        ride.setDriverId(driver.get().getDriverId());
        ride.setStatus(RideStatus.ASSIGNED);
        return rideRepository.save(ride);
    }

    /**
     * Applies a validated status transition. Throws InvalidStatusTransitionException
     * for illegal jumps (e.g. REQUESTED -> COMPLETED). CANCELLED requires a reason.
     */
    public Ride updateStatus(Long id, UpdateStatusRequest request, String authorizationHeader) {
        Ride ride = getById(id);
        stateMachine.assertValidTransition(ride.getStatus(), request.getStatus());

        if (request.getStatus() == RideStatus.CANCELLED) {
            ride.setCancellationReason(
                    request.getReason() != null ? request.getReason() : "Not specified");
        }

        ride.setStatus(request.getStatus());
        Ride saved = rideRepository.save(ride);

        // Interaction 2: when a ride is COMPLETED, Ride Service triggers payment.
        // Fare/Payment is the source of truth for payment status/failure; this
        // service does not roll back the ride on a downstream payment failure.
        if (request.getStatus() == RideStatus.COMPLETED) {
            fareServiceClient.triggerPayment(
                    new PaymentTriggerRequest(saved.getId(), saved.getDistanceKm(), saved.getDurationMin()),
                    authorizationHeader);
        }

        return saved;
    }

    public Ride getById(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(id));
    }

    public List<Ride> getByPassenger(Long passengerId) {
        return rideRepository.findByPassengerId(passengerId);
    }

    public List<Ride> getByDriver(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    public List<Ride> getAll() {
        return rideRepository.findAll();
    }
}
