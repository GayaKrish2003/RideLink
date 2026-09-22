package com.ridelink.accountservice.dto;

import com.ridelink.accountservice.model.User;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

// This is the shape of data we send BACK after successful registration.
// Deliberately excludes passwordHash — that should never leave the server.
@Getter
@Builder
public class RegisterResponse {

    private UUID id;
    private String name;
    private String email;
    private User.Role role;
    private User.Status status;

    // Helper to convert a User entity into this safe response shape.
    // Keeps the "never expose passwordHash" rule enforced in one place.
    public static RegisterResponse fromUser(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}