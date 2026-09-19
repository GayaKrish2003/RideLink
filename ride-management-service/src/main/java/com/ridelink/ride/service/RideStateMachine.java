package com.ridelink.ride.service;

import com.ridelink.ride.entity.RideStatus;
import com.ridelink.ride.exception.InvalidStatusTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Documents and enforces the valid ride lifecycle transitions:
 * REQUESTED -> ASSIGNED -> ACCEPTED -> IN_PROGRESS -> COMPLETED
 * with CANCELLED reachable from any non-terminal state.
 * Rejects invalid jumps, e.g. REQUESTED -> COMPLETED.
 */
@Component
public class RideStateMachine {

    private static final Map<RideStatus, Set<RideStatus>> ALLOWED = new EnumMap<>(RideStatus.class);

    static {
        ALLOWED.put(RideStatus.REQUESTED, EnumSet.of(RideStatus.ASSIGNED, RideStatus.CANCELLED));
        ALLOWED.put(RideStatus.ASSIGNED, EnumSet.of(RideStatus.ACCEPTED, RideStatus.CANCELLED));
        ALLOWED.put(RideStatus.ACCEPTED, EnumSet.of(RideStatus.IN_PROGRESS, RideStatus.CANCELLED));
        ALLOWED.put(RideStatus.IN_PROGRESS, EnumSet.of(RideStatus.COMPLETED, RideStatus.CANCELLED));
        ALLOWED.put(RideStatus.COMPLETED, EnumSet.noneOf(RideStatus.class));
        ALLOWED.put(RideStatus.CANCELLED, EnumSet.noneOf(RideStatus.class));
    }

    public void assertValidTransition(RideStatus from, RideStatus to) {
        if (from == to) {
            throw new InvalidStatusTransitionException(from, to);
        }
        Set<RideStatus> allowedNext = ALLOWED.getOrDefault(from, EnumSet.noneOf(RideStatus.class));
        if (!allowedNext.contains(to)) {
            throw new InvalidStatusTransitionException(from, to);
        }
    }
}
