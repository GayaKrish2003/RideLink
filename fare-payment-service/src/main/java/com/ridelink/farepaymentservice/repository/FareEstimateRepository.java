package com.ridelink.farepaymentservice.repository;

import com.ridelink.farepaymentservice.model.FareEstimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FareEstimateRepository extends JpaRepository<FareEstimate, Long> {
}