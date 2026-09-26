package com.ridelink.ride.service;

import com.ridelink.ride.entity.RideStatus;
import com.ridelink.ride.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RideStateMachineTest {

    private final RideStateMachine stateMachine = new RideStateMachine();

    @Test
    void allowsFullHappyPathLifecycle() {
        assertDoesNotThrow(() -> stateMachine.assertValidTransition(RideStatus.REQUESTED, RideStatus.ASSIGNED));
        assertDoesNotThrow(() -> stateMachine.assertValidTransition(RideStatus.ASSIGNED, RideStatus.ACCEPTED));
        assertDoesNotThrow(() -> stateMachine.assertValidTransition(RideStatus.ACCEPTED, RideStatus.IN_PROGRESS));
        assertDoesNotThrow(() -> stateMachine.assertValidTransition(RideStatus.IN_PROGRESS, RideStatus.COMPLETED));
    }

    @Test
    void allowsCancellationFromAnyNonTerminalState() {
        assertDoesNotThrow(() -> stateMachine.assertValidTransition(RideStatus.REQUESTED, RideStatus.CANCELLED));
        assertDoesNotThrow(() -> stateMachine.assertValidTransition(RideStatus.ASSIGNED, RideStatus.CANCELLED));
        assertDoesNotThrow(() -> stateMachine.assertValidTransition(RideStatus.ACCEPTED, RideStatus.CANCELLED));
        assertDoesNotThrow(() -> stateMachine.assertValidTransition(RideStatus.IN_PROGRESS, RideStatus.CANCELLED));
    }

    @Test
    void rejectsInvalidJumpFromRequestedToCompleted() {
        assertThrows(InvalidStatusTransitionException.class,
                () -> stateMachine.assertValidTransition(RideStatus.REQUESTED, RideStatus.COMPLETED));
    }

    @Test
    void rejectsAnyTransitionOutOfCompleted() {
        assertThrows(InvalidStatusTransitionException.class,
                () -> stateMachine.assertValidTransition(RideStatus.COMPLETED, RideStatus.IN_PROGRESS));
    }

    @Test
    void rejectsAnyTransitionOutOfCancelled() {
        assertThrows(InvalidStatusTransitionException.class,
                () -> stateMachine.assertValidTransition(RideStatus.CANCELLED, RideStatus.ASSIGNED));
    }

    @Test
    void rejectsNoOpTransition() {
        assertThrows(InvalidStatusTransitionException.class,
                () -> stateMachine.assertValidTransition(RideStatus.ASSIGNED, RideStatus.ASSIGNED));
    }
}
