package com.ridelink.accountservice.dto;

import lombok.Builder;
import lombok.Getter;

// Shape of data returned after a successful login.
// Includes the JWT the client will use for future authenticated requests,
// plus a few basic user details for convenience (never passwordHash).
@Getter
@Builder
public class LoginResponse {
    private String token;
    private String name;
    private String role;
}