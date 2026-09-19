package com.ridelink.ride.controller;

import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.dto.UpdateStatusRequest;
import com.ridelink.ride.entity.Ride;
import com.ridelink.ride.service.RideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest request,
                                                    @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        Ride ride = rideService.createRide(request, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(RideResponse.from(ride));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRide(@PathVariable Long id) {
        return ResponseEntity.ok(RideResponse.from(rideService.getById(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RideResponse> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateStatusRequest request,
                                                       @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        Ride ride = rideService.updateStatus(id, request, authHeader);
        return ResponseEntity.ok(RideResponse.from(ride));
    }

    @GetMapping
    public ResponseEntity<?> listRides(@RequestParam(required = false) Long passengerId,
                                        @RequestParam(required = false) Long driverId) {
        if (passengerId != null) {
            return ResponseEntity.ok(rideService.getByPassenger(passengerId).stream().map(RideResponse::from).toList());
        }
        if (driverId != null) {
            return ResponseEntity.ok(rideService.getByDriver(driverId).stream().map(RideResponse::from).toList());
        }
        return ResponseEntity.ok(rideService.getAll().stream().map(RideResponse::from).toList());
    }
}
