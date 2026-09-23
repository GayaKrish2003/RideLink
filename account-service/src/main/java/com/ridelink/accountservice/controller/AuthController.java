package com.ridelink.accountservice.controller;

import com.ridelink.accountservice.dto.RegisterRequest;
import com.ridelink.accountservice.dto.RegisterResponse;
import com.ridelink.accountservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ridelink.accountservice.dto.LoginRequest;
import com.ridelink.accountservice.dto.LoginResponse;
import com.ridelink.accountservice.dto.ProfileResponse;
import com.ridelink.accountservice.dto.UpdateProfileRequest;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // Spring injects AuthService automatically via constructor.
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register
    // @Valid triggers the validation annotations on RegisterRequest
    // (e.g. @NotBlank, @Email, @Size) — if any fail, Spring automatically
    // returns a 400 Bad Request with details, before this method even runs.
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);

        // 201 Created is the correct status for "a new resource was made",
        // as opposed to 200 OK which implies something already existing.
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/auth/login
    // Takes email + password, returns a JWT if credentials are valid.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        // 200 OK is correct here — login doesn't create a new resource,
        // it just returns a token for an existing one.
        return ResponseEntity.ok(response);
    }

    // GET /api/auth/profile
    // Reads the caller's identity from their JWT (sent in the Authorization header)
    // and returns their profile. No request body needed — the token IS the input.
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        ProfileResponse response = authService.getProfile(token);
        return ResponseEntity.ok(response);
    }

    // PUT /api/auth/profile
    // Updates the caller's own profile. Same token-based identity check,
    // plus a request body containing the new data.
    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateProfileRequest request) {

        String token = extractToken(authHeader);
        ProfileResponse response = authService.updateProfile(token, request);
        return ResponseEntity.ok(response);
    }

    // Helper: the Authorization header arrives as "Bearer <token>" —
    // this strips the "Bearer " prefix to get just the raw token string.
    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Missing or invalid Authorization header");
        }
        return authHeader.substring(7); // "Bearer " is 7 characters
    }
}