package com.ridelink.accountservice.dto;

import com.ridelink.accountservice.model.User;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

// Safe shape for returning a user's own profile — same exclusion
// principle as RegisterResponse: never includes passwordHash.
@Getter
@Builder
public class ProfileResponse {
    private UUID id;
    private String name;
    private String email;
    private User.Role role;
    private User.Status status;

    public static ProfileResponse fromUser(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}