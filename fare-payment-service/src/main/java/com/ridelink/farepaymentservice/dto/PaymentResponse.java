package com.ridelink.farepaymentservice.dto;

import com.ridelink.farepaymentservice.model.PaymentStatus;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long rideId;
    private double amount;
    private String paymentMethod;
    private PaymentStatus status;
    private LocalDateTime createdAt;

    public PaymentResponse(Long id, Long rideId, double amount, String paymentMethod,
                           PaymentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.rideId = rideId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters only
    public Long getId() { return id; }
    public Long getRideId() { return rideId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}