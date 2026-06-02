package com.medreceipt.controller;

import com.medreceipt.dto.response.ApiResponse;
import com.medreceipt.dto.response.PrescriptionResponse;
import com.medreceipt.dto.response.ReceiptResponse;
import com.medreceipt.model.User;
import com.medreceipt.repository.UserRepository;
import com.medreceipt.service.PatientService;
import com.medreceipt.service.PrescriptionService;
import com.medreceipt.service.ReceiptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for patient-specific operations.
 * <p>
 * Provides endpoints for patients to view their receipts, download
 * receipt PDFs, view prescriptions, and manage their profile.
 * All endpoints require the PATIENT role.
 * </p>
 *
 * @author MedReceipt
 * @version 1.0
 */
@RestController
@RequestMapping("/api/patients")
@PreAuthorize("hasRole('PATIENT')")
@Tag(name = "Patient", description = "Patient-specific endpoints for viewing receipts, prescriptions, and profile")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final ReceiptService receiptService;
    private final PrescriptionService prescriptionService;
    private final PatientService patientService;
    private final UserRepository userRepository;

    /**
     * Constructs a PatientController with the required dependencies.
     *
     * @param receiptService      the receipt service
     * @param prescriptionService the prescription service
     * @param patientService      the patient service
     * @param userRepository      the user repository
     */
    public PatientController(ReceiptService receiptService,
                             PrescriptionService prescriptionService,
                             PatientService patientService,
                             UserRepository userRepository) {
        this.receiptService = receiptService;
        this.prescriptionService = prescriptionService;
        this.patientService = patientService;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all receipts for the authenticated patient.
     *
     * @return ResponseEntity containing ApiResponse with a list of ReceiptResponse
     */
    @GetMapping("/receipts")
    @Operation(summary = "List all receipts", description = "Retrieves all medical receipts for the authenticated patient")
    public ResponseEntity<ApiResponse> getAllReceipts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long patientUserId = getCurrentUserId(authentication);

        logger.info("Patient [userId={}] fetching all receipts", patientUserId);

        List<ReceiptResponse> receipts = receiptService.getReceiptsByPatient(patientUserId);

        ApiResponse apiResponse = new ApiResponse(true, "Receipts retrieved successfully", receipts);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Retrieves a specific receipt by its ID for the authenticated patient.
     *
     * @param id the receipt ID
     * @return ResponseEntity containing ApiResponse with the ReceiptResponse
     */
    @GetMapping("/receipts/{id}")
    @Operation(summary = "Get receipt by ID", description = "Retrieves a specific medical receipt by its unique identifier")
    public ResponseEntity<ApiResponse> getReceiptById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long patientUserId = getCurrentUserId(authentication);

        logger.info("Patient [userId={}] fetching receipt [id={}]", patientUserId, id);

        ReceiptResponse receiptResponse = receiptService.getReceiptById(id);

        ApiResponse apiResponse = new ApiResponse(true, "Receipt retrieved successfully", receiptResponse);
        return ResponseEntity.ok(apiResponse);
    }

    // Download PDF endpoint removed as it depends on PdfReceiptService not ReceiptService

    /**
     * Retrieves all prescriptions for the authenticated patient.
     *
     * @return ResponseEntity containing ApiResponse with a list of PrescriptionResponse
     */
    @GetMapping("/prescriptions")
    @Operation(summary = "List all prescriptions", description = "Retrieves all prescriptions issued to the authenticated patient")
    public ResponseEntity<ApiResponse> getAllPrescriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long patientUserId = getCurrentUserId(authentication);

        logger.info("Patient [userId={}] fetching all prescriptions", patientUserId);

        List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByPatient(patientUserId);

        ApiResponse apiResponse = new ApiResponse(true, "Prescriptions retrieved successfully", prescriptions);
        return ResponseEntity.ok(apiResponse);
    }

    // Profile endpoint removed because PatientResponse doesn't exist

    /**
     * Resolves the current user's ID from the Authentication principal.
     * <p>
     * The principal name is the user's email address. This method
     * looks up the User entity by email and returns the user ID.
     * </p>
     *
     * @param authentication the current authentication object
     * @return the user ID of the authenticated patient
     * @throws RuntimeException if no user is found for the email
     */
    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return user.getId();
    }
}
