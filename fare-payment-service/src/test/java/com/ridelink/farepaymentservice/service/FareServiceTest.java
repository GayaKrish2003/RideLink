package com.ridelink.farepaymentservice.service;

import com.ridelink.farepaymentservice.model.FareEstimate;
import com.ridelink.farepaymentservice.repository.FareEstimateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FareServiceTest {

    @Mock
    private FareEstimateRepository fareEstimateRepository;

    @InjectMocks
    private FareService fareService;

    @Test
    void estimateFare_calculatesCorrectFare_forValidInput() {
        when(fareEstimateRepository.save(any(FareEstimate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FareEstimate result = fareService.estimateFare("Maharagama", "Colombo Fort", 12.5);

        assertEquals(725.0, result.getEstimatedFare());
        assertEquals("Maharagama", result.getPickupLocation());
        assertEquals("Colombo Fort", result.getDestination());
    }

    @Test
    void estimateFare_throwsException_whenDistanceIsZero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fareService.estimateFare("A", "B", 0)
        );
        assertEquals("Distance must be greater than zero", exception.getMessage());
    }

    @Test
    void estimateFare_throwsException_whenDistanceIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> fareService.estimateFare("A", "B", -5)
        );
    }
}