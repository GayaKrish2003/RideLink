package com.ridelink.ride.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Interaction 1 (finalized, synchronous REST):
 * Ride Management -> Driver & Vehicle Service, GET /api/drivers/available.
 * Called when a passenger creates a ride request, so an eligible driver
 * can be assigned immediately before the ride is confirmed.
 */
@Component
public class DriverServiceClient {

    private final RestTemplate restTemplate;
    private final String driverServiceBaseUrl;

    public DriverServiceClient(RestTemplate restTemplate,
                                @Value("${services.driver-service.base-url}") String driverServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.driverServiceBaseUrl = driverServiceBaseUrl;
    }

    public Optional<AvailableDriverDto> findFirstAvailableDriver(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (authorizationHeader != null) {
            headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        AvailableDriverDto[] drivers = restTemplate.exchange(
                driverServiceBaseUrl + "/api/drivers/available",
                HttpMethod.GET,
                entity,
                AvailableDriverDto[].class
        ).getBody();

        if (drivers == null || drivers.length == 0) {
            return Optional.empty();
        }
        // Documented, simple assignment approach: take the first eligible driver returned.
        return Optional.of(drivers[0]);
    }
}
