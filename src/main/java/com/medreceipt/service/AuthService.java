package com.medreceipt.service;

import com.medreceipt.dto.request.LoginRequest;
import com.medreceipt.dto.request.RegisterRequest;
import com.medreceipt.dto.response.AuthResponse;
import com.medreceipt.model.Doctor;
import com.medreceipt.model.Patient;
import com.medreceipt.model.User;
import com.medreceipt.model.enums.Role;
import com.medreceipt.repository.DoctorRepository;
import com.medreceipt.repository.PatientRepository;
import com.medreceipt.repository.UserRepository;
import com.medreceipt.security.JwtTokenProvider;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for authentication operations including user registration and login.
 * <p>
 * Handles the creation of {@link User} entities along with their corresponding role-specific
 * profiles ({@link Doctor} or {@link Patient}), password encoding, and JWT token generation.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs the AuthService with all required dependencies.
     *
     * @param userRepository        repository for user persistence
     * @param doctorRepository      repository for doctor persistence
     * @param patientRepository     repository for patient persistence
     * @param passwordEncoder       encoder for hashing passwords
     * @param authenticationManager Spring Security authentication manager
     * @param jwtTokenProvider      provider for generating JWT tokens
     */
    public AuthService(UserRepository userRepository,
                       DoctorRepository doctorRepository,
                       PatientRepository patientRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new user in the system.
     * <p>
     * Creates a {@link User} entity with encoded password and the specified role.
     * If the role is {@code ROLE_DOCTOR}, a corresponding {@link Doctor} profile is created
     * with specialization and license number. If the role is {@code ROLE_PATIENT}, a
     * corresponding {@link Patient} profile is created with date of birth and address.
     * </p>
     * <p>
     * A JWT token is generated upon successful registration and returned in the response.
     * </p>
     *
     * @param request the registration request containing user details
     * @return an {@link AuthResponse} containing the JWT token and user information
     * @throws DuplicateResourceException if a user with the given email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: email already exists - {}", request.getEmail());
            throw new IllegalArgumentException("User with email already exists: " + request.getEmail());
        }

        // Create and populate User entity
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.valueOf(request.getRole()));

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {} and role: {}", savedUser.getId(), savedUser.getRole());

        // Create role-specific profile
        if (Role.ROLE_DOCTOR == user.getRole()) {
            Doctor doctor = new Doctor();
            doctor.setUser(savedUser);
            doctor.setSpecialization(request.getSpecialization());
            doctor.setLicenseNumber(request.getLicenseNumber());
            doctorRepository.save(doctor);
            log.info("Doctor profile created for user ID: {} with license: {}",
                    savedUser.getId(), request.getLicenseNumber());

        } else if (Role.ROLE_PATIENT == user.getRole()) {
            Patient patient = new Patient();
            patient.setUser(savedUser);
            patient.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
            patient.setAddress(request.getAddress());
            patient.setBloodGroup(request.getBloodGroup());
            patientRepository.save(patient);
            log.info("Patient profile created for user ID: {}", savedUser.getId());
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getId(), savedUser.getRole().name());
        log.info("JWT token generated for newly registered user: {}", savedUser.getEmail());

        return buildAuthResponse(token, savedUser);
    }

    /**
     * Authenticates a user and generates a JWT token.
     * <p>
     * Uses Spring Security's {@link AuthenticationManager} to validate credentials.
     * Upon successful authentication, a JWT token is generated and returned.
     * </p>
     *
     * @param request the login request containing email and password
     * @return an {@link AuthResponse} containing the JWT token and user information
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found after successful authentication: {}", request.getEmail());
                    return new RuntimeException("User not found");
                });

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        log.info("Login successful for user: {} with role: {}", user.getEmail(), user.getRole());

        return buildAuthResponse(token, user);
    }

    /**
     * Builds an {@link AuthResponse} from a JWT token and user entity.
     *
     * @param token the JWT token
     * @param user  the authenticated user
     * @return a populated AuthResponse
     */
    private AuthResponse buildAuthResponse(String token, User user) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        return response;
    }
}
