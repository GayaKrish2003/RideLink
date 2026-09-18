package com.ridelink.farepaymentservice.controller;

import com.ridelink.farepaymentservice.dto.FareEstimateRequest;
import com.ridelink.farepaymentservice.dto.FareEstimateResponse;
import com.ridelink.farepaymentservice.model.FareEstimate;
import com.ridelink.farepaymentservice.service.FareService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
@RestController
@RequestMapping("/api/fares")

public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @Operation(summary = "Estimate a fare", description = "Calculates an estimated fare based on pickup, destination, and distance using the formula: base fare + (distance × rate).")
    @PostMapping("/estimate")
    public ResponseEntity<FareEstimateResponse> estimateFare(@Valid @RequestBody FareEstimateRequest request) {
        FareEstimate estimate = fareService.estimateFare(
                request.getPickupLocation(),
                request.getDestination(),
                request.getDistanceKm()
        );

        FareEstimateResponse response = new FareEstimateResponse(
                estimate.getId(),
                estimate.getPickupLocation(),
                estimate.getDestination(),
                estimate.getDistanceKm(),
                estimate.getEstimatedFare(),
                estimate.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}