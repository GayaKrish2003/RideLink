package com.ridelink.farepaymentservice.service;

import com.ridelink.farepaymentservice.model.Payment;
import com.ridelink.farepaymentservice.model.PaymentStatus;
import com.ridelink.farepaymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPayment_calculatesCorrectFare_forCashPayment() {
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.processPayment(1L, 12.5, 20, "CASH");

        // Formula: base(100) + distance*50 + duration*5 = 100 + 625 + 100 = 825
        assertEquals(825.0, result.getAmount());
        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
        assertEquals("CASH", result.getPaymentMethod());
    }

    @Test
    void processPayment_alwaysFails_whenRideIdIs999AndCardUsed() {
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.processPayment(999L, 8, 15, "CARD");

        assertEquals(PaymentStatus.FAILED, result.getStatus());
    }

    @Test
    void processPayment_throwsException_whenRideIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(null, 10, 15, "CASH")
        );
    }

    @Test
    void processPayment_throwsException_whenDistanceIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(1L, 0, 15, "CASH")
        );
    }

    @Test
    void getPaymentById_throwsNotFoundException_whenPaymentDoesNotExist() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentById(999L)
        );
        assertEquals("Payment not found with id: 999", exception.getMessage());
    }

    @Test
    void getPaymentById_returnsPayment_whenPaymentExists() {
        Payment payment = new Payment(1L, 725.0, "CASH", PaymentStatus.COMPLETED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById(1L);

        assertEquals(725.0, result.getAmount());
    }
}