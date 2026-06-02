package com.medreceipt.controller;

import com.medreceipt.dto.request.LoginRequest;
import com.medreceipt.dto.request.RegisterRequest;
import com.medreceipt.dto.response.ApiResponse;
import com.medreceipt.dto.response.AuthResponse;
import com.medreceipt.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication operations.
 * <p>
 * Provides endpoints for user registration and login. Both endpoints
 * are publicly accessible (no authentication required) and return
 * JWT tokens upon successful authentication.
 * </p>
 *
 * @author MedReceipt
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Constructs an AuthController with the required AuthService dependency.
     *
     * @param authService the authentication service
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user in the system.
     * <p>
     * Accepts user registration details including name, email, password,
     * and role. Returns a JWT token upon successful registration.
     * </p>
     *
     * @param registerRequest the registration request containing user details
     * @return ResponseEntity containing ApiResponse with AuthResponse (JWT token and user info)
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT authentication token")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        logger.info("Registration request received for email: {}", registerRequest.getEmail());

        AuthResponse authResponse = authService.register(registerRequest);

        logger.info("User registered successfully with email: {}", registerRequest.getEmail());

        ApiResponse apiResponse = new ApiResponse(true, "User registered successfully", authResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /**
     * Authenticates an existing user.
     * <p>
     * Accepts login credentials (email and password) and returns a
     * JWT token upon successful authentication.
     * </p>
     *
     * @param loginRequest the login request containing email and password
     * @return ResponseEntity containing ApiResponse with AuthResponse (JWT token and user info)
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate a user", description = "Validates credentials and returns a JWT authentication token")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        logger.info("Login request received for email: {}", loginRequest.getEmail());

        AuthResponse authResponse = authService.login(loginRequest);

        logger.info("User logged in successfully with email: {}", loginRequest.getEmail());

        ApiResponse apiResponse = new ApiResponse(true, "Login successful", authResponse);
        return ResponseEntity.ok(apiResponse);
    }
}
