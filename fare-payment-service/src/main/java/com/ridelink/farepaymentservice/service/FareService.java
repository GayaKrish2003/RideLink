package com.ridelink.farepaymentservice.service;

import com.ridelink.farepaymentservice.model.FareEstimate;
import com.ridelink.farepaymentservice.repository.FareEstimateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FareService {

    private static final double BASE_FARE = 100.0;
    private static final double PER_KM_RATE = 50.0;

    private final FareEstimateRepository fareEstimateRepository;

    @Autowired
    public FareService(FareEstimateRepository fareEstimateRepository) {
        this.fareEstimateRepository = fareEstimateRepository;
    }

    public FareEstimate estimateFare(String pickupLocation, String destination, double distanceKm) {
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero");
        }

        double estimatedFare = BASE_FARE + (distanceKm * PER_KM_RATE);

        FareEstimate estimate = new FareEstimate(pickupLocation, destination, distanceKm, estimatedFare);
        return fareEstimateRepository.save(estimate);
    }
}