package com.ridelink.farepaymentservice.service;

import com.ridelink.farepaymentservice.model.Payment;
import com.ridelink.farepaymentservice.model.PaymentStatus;
import com.ridelink.farepaymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class PaymentService {

    private static final double BASE_FARE = 100.0;
    private static final double PER_KM_RATE = 50.0;
    private static final double PER_MINUTE_RATE = 5.0;
    private static final double CARD_FAILURE_RATE = 0.10; // 10% simulated failure chance

    private final PaymentRepository paymentRepository;
    private final Random random = new Random();

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(Long rideId, double distanceKm, double durationMin, String paymentMethod) {
        if (rideId == null) {
            throw new IllegalArgumentException("Ride ID is required");
        }
        if (distanceKm <= 0 || durationMin <= 0) {
            throw new IllegalArgumentException("Distance and duration must be greater than zero");
        }

        double finalFare = BASE_FARE + (distanceKm * PER_KM_RATE) + (durationMin * PER_MINUTE_RATE);

        Payment payment = new Payment(rideId, finalFare, paymentMethod, PaymentStatus.PENDING);

        boolean simulatedFailure = "CARD".equalsIgnoreCase(paymentMethod)
                && (rideId == 999L || random.nextDouble() < CARD_FAILURE_RATE);

        if (simulatedFailure) {
            payment.setStatus(PaymentStatus.FAILED);
        } else {
            payment.setStatus(PaymentStatus.COMPLETED);
        }

        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}