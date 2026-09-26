package com.ridelink.ride.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Interaction 2 (finalized, synchronous REST):
 * Ride Management -> Fare & Payment Service, POST /api/payments.
 * Called when a ride's status changes to COMPLETED, because payment
 * must be confirmed before the ride is fully closed.
 */
@Component
public class FareServiceClient {

    private final RestTemplate restTemplate;
    private final String fareServiceBaseUrl;

    public FareServiceClient(RestTemplate restTemplate,
                              @Value("${services.fare-service.base-url}") String fareServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.fareServiceBaseUrl = fareServiceBaseUrl;
    }

    public void triggerPayment(PaymentTriggerRequest request, String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        if (authorizationHeader != null) {
            headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }
        HttpEntity<PaymentTriggerRequest> entity = new HttpEntity<>(request, headers);
        restTemplate.postForEntity(fareServiceBaseUrl + "/api/payments", entity, Void.class);
    }
}
