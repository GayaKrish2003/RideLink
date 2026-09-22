package com.ridelink.accountservice.dto;

import com.ridelink.accountservice.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// This is the shape of data a client sends to POST /api/auth/register.
// We NEVER let clients send a User entity directly — that would let them
// set fields like id, status, or passwordHash themselves, which is unsafe.
// This DTO only exposes exactly what registration should require.
@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    // Plain text password from the client — this gets hashed with BCrypt
    // in the service layer before it's ever stored. Never saved as-is.
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // Role the user is registering as — PASSENGER or DRIVER.
    // (ADMIN accounts shouldn't be self-registered via this public endpoint —
    // that would be a security hole. Admins would be created differently,
    // e.g. manually or via a separate protected endpoint later if needed.)
    @NotNull(message = "Role is required")
    private User.Role role;
}