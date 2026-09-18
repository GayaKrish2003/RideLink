package com.ridelink.farepaymentservice.controller;

import com.ridelink.farepaymentservice.dto.PaymentRequest;
import com.ridelink.farepaymentservice.dto.PaymentResponse;
import com.ridelink.farepaymentservice.model.Payment;
import com.ridelink.farepaymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.processPayment(
                request.getRideId(),
                request.getDistanceKm(),
                request.getDurationMin(),
                request.getPaymentMethod()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(toResponse(payment));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> responses = paymentService.getAllPayments()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getRideId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}