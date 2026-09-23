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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class) enables Mockito annotations (@Mock, @InjectMocks)
// without needing a full Spring context — this is what makes it a fast unit test,
// not an integration test.
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // @Mock creates a fake UserRepository and JwtUtil — we control exactly
    // what they return, so we're testing ONLY AuthService's own logic.
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    // @InjectMocks creates a real AuthService and automatically wires
    // the mocks above into its constructor.
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
        // Arrange: tell the mock repository "this email doesn't exist yet"
        // and "when save() is called, just return whatever User was passed in"
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID()); // simulate the DB assigning an ID
            return u;
        });

        // Act
        RegisterResponse response = authService.register(validRegisterRequest);

        // Assert
        assertNotNull(response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(User.Role.PASSENGER, response.getRole());
        assertEquals(User.Status.ACTIVE, response.getStatus());

        // Verify save() was actually called exactly once
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_withDuplicateEmail_throwsConflict() {
        // Arrange: simulate the email already existing
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act + Assert: expect an exception, and check it's the right one
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(validRegisterRequest));

        assertEquals(409, ex.getStatusCode().value());

        // Confirm we never even tried to save — the duplicate check
        // should short-circuit before reaching the database write.
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_withAdminRole_throwsForbidden() {
        // Arrange: attempt to register as ADMIN
        validRegisterRequest.setRole(User.Role.ADMIN);

        // Act + Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(validRegisterRequest));

        assertEquals(403, ex.getStatusCode().value());
        verify(userRepository, never()).save(any(User.class));
    }

    // --- LOGIN TESTS ---

    @Test
    void login_withCorrectCredentials_returnsTokenAndUserInfo() {
        // Arrange: build a fake existing user with a real BCrypt hash,
        // so the password comparison inside login() actually works correctly.
        String rawPassword = "password123";
        String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .passwordHash(hashedPassword)
                .role(User.Role.PASSENGER)
                .status(User.Status.ACTIVE)
                .build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword(rawPassword);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(existingUser.getId(), "PASSENGER"))
                .thenReturn("fake-jwt-token");

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("Test User", response.getName());
        assertEquals("PASSENGER", response.getRole());
    }

    @Test
    void login_withWrongPassword_throwsUnauthorized() {
        String hashedPassword = new BCryptPasswordEncoder().encode("correctPassword");

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash(hashedPassword)
                .role(User.Role.PASSENGER)
                .status(User.Status.ACTIVE)
                .build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));

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
        String hashedPassword = new BCryptPasswordEncoder().encode("password123");

        User deactivatedUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash(hashedPassword)
                .role(User.Role.PASSENGER)
                .status(User.Status.DEACTIVATED) // key part of this test
                .build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(deactivatedUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(loginRequest));

        assertEquals(403, ex.getStatusCode().value());
    }
}