package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.LoginRequest;
import com.ridelink.accountservice.dto.LoginResponse;
import com.ridelink.accountservice.dto.RegisterRequest;
import com.ridelink.accountservice.dto.RegisterResponse;
import com.ridelink.accountservice.model.User;
import com.ridelink.accountservice.repository.UserRepository;
import com.ridelink.accountservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRegisterRequest;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setName("Test User");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setRole(User.Role.PASSENGER);
    }

    // --- REGISTER TESTS ---

    @Test
    void register_withValidData_savesUserAndReturnsResponse() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        // Since passwordEncoder is now a mock, tell it what to return
        // when encode() is called — otherwise it returns null by default.
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        RegisterResponse response = authService.register(validRegisterRequest);

        assertNotNull(response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(User.Role.PASSENGER, response.getRole());
        assertEquals(User.Status.ACTIVE, response.getStatus());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_withDuplicateEmail_throwsConflict() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(validRegisterRequest));

        assertEquals(409, ex.getStatusCode().value());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_withAdminRole_throwsForbidden() {
        validRegisterRequest.setRole(User.Role.ADMIN);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(validRegisterRequest));

        assertEquals(403, ex.getStatusCode().value());
        verify(userRepository, never()).save(any(User.class));
    }

    // --- LOGIN TESTS ---

    @Test
    void login_withCorrectCredentials_returnsTokenAndUserInfo() {
        String rawPassword = "password123";
        String storedHash = "hashed-password"; // arbitrary placeholder — the mock
        // controls the "match" result below,
        // not real BCrypt comparison

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .passwordHash(storedHash)
                .role(User.Role.PASSENGER)
                .status(User.Status.ACTIVE)
                .build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword(rawPassword);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        // Tell the mock: "when comparing this raw password against this hash, say yes"
        when(passwordEncoder.matches(rawPassword, storedHash)).thenReturn(true);
        when(jwtUtil.generateToken(existingUser.getId(), "PASSENGER"))
                .thenReturn("fake-jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("Test User", response.getName());
        assertEquals("PASSENGER", response.getRole());
    }

    @Test
    void login_withWrongPassword_throwsUnauthorized() {
        String storedHash = "hashed-password";

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash(storedHash)
                .role(User.Role.PASSENGER)
                .status(User.Status.ACTIVE)
                .build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        // Tell the mock: "this password does NOT match"
        when(passwordEncoder.matches("wrongPassword", storedHash)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(loginRequest));

        assertEquals(401, ex.getStatusCode().value());
    }

    @Test
    void login_withNonExistentEmail_throwsUnauthorized() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("doesnotexist@example.com");
        loginRequest.setPassword("anyPassword");

        when(userRepository.findByEmail("doesnotexist@example.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(loginRequest));

        assertEquals(401, ex.getStatusCode().value());
    }

    @Test
    void login_onDeactivatedAccount_throwsForbidden() {
        String storedHash = "hashed-password";

        User deactivatedUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash(storedHash)
                .role(User.Role.PASSENGER)
                .status(User.Status.DEACTIVATED)
                .build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(deactivatedUser));
        when(passwordEncoder.matches("password123", storedHash)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(loginRequest));

        assertEquals(403, ex.getStatusCode().value());
    }
}