package com.medreceipt.controller;

import com.medreceipt.dto.request.PrescriptionRequest;
import com.medreceipt.dto.request.ReceiptRequest;
import com.medreceipt.dto.response.ApiResponse;
import com.medreceipt.dto.response.PrescriptionResponse;
import com.medreceipt.dto.response.ReceiptResponse;
import com.medreceipt.model.User;
import com.medreceipt.repository.UserRepository;
import com.medreceipt.service.DoctorService;
import com.medreceipt.service.PrescriptionService;
import com.medreceipt.service.ReceiptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for doctor-specific operations.
 * <p>
 * Provides endpoints for managing prescriptions, generating receipts,
 * and viewing patient information. All endpoints require the DOCTOR role.
 * The authenticated doctor is identified from the security context.
 * </p>
 *
 * @author MedReceipt
 * @version 1.0
 */
@RestController
@RequestMapping("/api/doctors")
@PreAuthorize("hasRole('DOCTOR')")
@Tag(name = "Doctor", description = "Doctor-specific endpoints for prescriptions, receipts, and patient management")
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    private final PrescriptionService prescriptionService;
    private final ReceiptService receiptService;
    private final DoctorService doctorService;
    private final UserRepository userRepository;

    /**
     * Constructs a DoctorController with the required dependencies.
     *
     * @param prescriptionService the prescription service
     * @param receiptService      the receipt service
     * @param doctorService       the doctor service
     * @param userRepository      the user repository
     */
    public DoctorController(PrescriptionService prescriptionService,
                            ReceiptService receiptService,
                            DoctorService doctorService,
                            UserRepository userRepository) {
        this.prescriptionService = prescriptionService;
        this.receiptService = receiptService;
        this.doctorService = doctorService;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new prescription for a patient.
     * <p>
     * The prescription is associated with the currently authenticated doctor.
     * The doctor's user ID is resolved from the security context.
     * </p>
     *
     * @param prescriptionRequest the prescription details
     * @return ResponseEntity containing ApiResponse with the created PrescriptionResponse
     */
    @PostMapping("/prescriptions")
    @Operation(summary = "Create a new prescription", description = "Creates a prescription for a patient under the authenticated doctor")
    public ResponseEntity<ApiResponse> createPrescription(@RequestBody @Valid PrescriptionRequest prescriptionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long doctorUserId = getCurrentUserId(authentication);

        logger.info("Doctor [userId={}] creating prescription for patient [id={}]",
                doctorUserId, prescriptionRequest.getPatientId());

        PrescriptionResponse prescriptionResponse = prescriptionService.createPrescription(doctorUserId, prescriptionRequest);

        logger.info("Prescription created successfully with id: {}", prescriptionResponse.getId());

        ApiResponse apiResponse = new ApiResponse(true, "Prescription created successfully", prescriptionResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /**
     * Retrieves all prescriptions created by the authenticated doctor.
     *
     * @return ResponseEntity containing ApiResponse with a list of PrescriptionResponse
     */
    @GetMapping("/prescriptions")
    @Operation(summary = "List all prescriptions", description = "Retrieves all prescriptions created by the authenticated doctor")
    public ResponseEntity<ApiResponse> getAllPrescriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long doctorUserId = getCurrentUserId(authentication);

        logger.info("Doctor [userId={}] fetching all prescriptions", doctorUserId);

        List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorUserId);

        ApiResponse apiResponse = new ApiResponse(true, "Prescriptions retrieved successfully", prescriptions);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Retrieves a specific prescription by its ID.
     *
     * @param id the prescription ID
     * @return ResponseEntity containing ApiResponse with the PrescriptionResponse
     */
    @GetMapping("/prescriptions/{id}")
    @Operation(summary = "Get prescription by ID", description = "Retrieves a specific prescription by its unique identifier")
    public ResponseEntity<ApiResponse> getPrescriptionById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long doctorUserId = getCurrentUserId(authentication);

        logger.info("Doctor [userId={}] fetching prescription [id={}]", doctorUserId, id);

        PrescriptionResponse prescriptionResponse = prescriptionService.getPrescriptionById(id);

        ApiResponse apiResponse = new ApiResponse(true, "Prescription retrieved successfully", prescriptionResponse);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Generates a digital receipt from an existing prescription.
     * <p>
     * Creates a receipt document (including PDF generation) based on
     * the specified prescription details.
     * </p>
     *
     * @param receiptRequest the receipt generation request containing prescription ID and details
     * @return ResponseEntity containing ApiResponse with the generated ReceiptResponse
     */
    @PostMapping("/receipts/generate")
    @Operation(summary = "Generate a receipt", description = "Generates a digital medical receipt from an existing prescription")
    public ResponseEntity<ApiResponse> generateReceipt(@RequestBody @Valid ReceiptRequest receiptRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long doctorUserId = getCurrentUserId(authentication);

        logger.info("Doctor [userId={}] generating receipt for prescription [id={}]",
                doctorUserId, receiptRequest.getPrescriptionId());

        ReceiptResponse receiptResponse = receiptService.generateReceipt(doctorUserId, receiptRequest);

        logger.info("Receipt generated successfully with receipt number: {}", receiptResponse.getReceiptNumber());

        ApiResponse apiResponse = new ApiResponse(true, "Receipt generated successfully", receiptResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /**
     * Retrieves all receipts generated by the authenticated doctor.
     *
     * @return ResponseEntity containing ApiResponse with a list of ReceiptResponse
     */
    @GetMapping("/receipts")
    @Operation(summary = "List all receipts", description = "Retrieves all receipts generated by the authenticated doctor")
    public ResponseEntity<ApiResponse> getAllReceipts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long doctorUserId = getCurrentUserId(authentication);

        logger.info("Doctor [userId={}] fetching all receipts", doctorUserId);

        List<ReceiptResponse> receipts = receiptService.getReceiptsByDoctor(doctorUserId);

        ApiResponse apiResponse = new ApiResponse(true, "Receipts retrieved successfully", receipts);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/patients")
    @Operation(summary = "List all patients", description = "Retrieves all patients in the system for prescription creation")
    public ResponseEntity<ApiResponse> getPatients() {
        List<com.medreceipt.dto.response.PatientResponse> patients = doctorService.getAllPatients();
        ApiResponse apiResponse = new ApiResponse(true, "Patients retrieved successfully", patients);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Resolves the current user's ID from the Authentication principal.
     * <p>
     * The principal name is the user's email address. This method
     * looks up the User entity by email and returns the user ID.
     * </p>
     *
     * @param authentication the current authentication object
     * @return the user ID of the authenticated doctor
     * @throws com.medreceipt.exception.ResourceNotFoundException if no user is found for the email
     */
    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return user.getId();
    }
}
