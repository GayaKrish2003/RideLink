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
}