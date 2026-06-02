package com.medreceipt.controller;

import com.medreceipt.dto.response.ApiResponse;
import com.medreceipt.dto.response.DrugVerificationResponse;
import com.medreceipt.model.Drug;
import com.medreceipt.repository.DrugRepository;
import com.medreceipt.service.DrugVerificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for drug-related operations.
 * <p>
 * Provides endpoints for searching drugs by name, verifying drugs
 * against the OpenFDA database, and retrieving drug details by ID.
 * The search endpoint is publicly accessible, while verification
 * requires authentication.
 * </p>
 *
 * @author MedReceipt
 * @version 1.0
 */
@RestController
@RequestMapping("/api/drugs")
@Tag(name = "Drug", description = "Drug search, verification, and retrieval endpoints")
public class DrugController {

    private static final Logger logger = LoggerFactory.getLogger(DrugController.class);

    private final DrugVerificationService drugVerificationService;
    private final DrugRepository drugRepository;

    /**
     * Constructs a DrugController with the required dependencies.
     *
     * @param drugVerificationService the drug verification service for OpenFDA lookups
     * @param drugRepository          the drug repository for database operations
     */
    public DrugController(DrugVerificationService drugVerificationService,
                          DrugRepository drugRepository) {
        this.drugVerificationService = drugVerificationService;
        this.drugRepository = drugRepository;
    }

    /**
     * Searches for drugs by name.
     * <p>
     * This endpoint is publicly accessible and performs a case-insensitive
     * partial match search against the drug database. Useful for autocomplete
     * and drug lookup features.
     * </p>
     *
     * @param name the drug name or partial name to search for
     * @return ResponseEntity containing ApiResponse with a list of matching DrugResponse objects
     */
    @GetMapping("/search")
    @Operation(summary = "Search drugs by name", description = "Searches for drugs by name with partial matching (publicly accessible)")
    public ResponseEntity<ApiResponse> searchDrugs(
            @RequestParam @Parameter(description = "Drug name or partial name to search for") String name) {

        logger.info("Searching for drugs with name containing: {}", name);

        List<DrugVerificationResponse> drugs = drugVerificationService.searchDrugs(name);

        logger.info("Found {} drug(s) matching: {}", drugs.size(), name);

        ApiResponse apiResponse = new ApiResponse(true, "Drug search completed successfully", drugs);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Verifies a drug against the OpenFDA database.
     * <p>
     * This endpoint requires authentication and queries the OpenFDA API
     * to verify drug information including safety data, interactions,
     * and approved usage. Results may be cached for performance.
     * </p>
     *
     * @param name the exact drug name to verify
     * @return ResponseEntity containing ApiResponse with the verification result
     */
    @GetMapping("/verify")
    @Operation(summary = "Verify drug via OpenFDA", description = "Verifies a specific drug against the OpenFDA database (authentication required)")
    public ResponseEntity<ApiResponse> verifyDrug(
            @RequestParam @Parameter(description = "Exact drug name to verify against OpenFDA") String name) {

        logger.info("Verifying drug against OpenFDA: {}", name);

        DrugVerificationResponse drugResponse = drugVerificationService.verifyDrug(name);

        logger.info("Drug verification completed for: {}", name);

        ApiResponse apiResponse = new ApiResponse(true, "Drug verification completed successfully", drugResponse);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Retrieves a drug by its unique database ID.
     *
     * @param id the drug ID
     * @return ResponseEntity containing ApiResponse with the DrugResponse
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get drug by ID", description = "Retrieves detailed information about a specific drug by its database ID")
    public ResponseEntity<ApiResponse> getDrugById(@PathVariable Long id) {
        logger.info("Fetching drug [id={}]", id);

        Drug drug = drugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drug not found with id: " + id));

        DrugVerificationResponse drugResponse = new DrugVerificationResponse();
        drugResponse.setBrandName(drug.getBrandName());
        drugResponse.setGenericName(drug.getGenericName());
        drugResponse.setManufacturer(drug.getManufacturer());
        drugResponse.setDosageForm(drug.getDosageForm());
        drugResponse.setVerified(drug.isVerified());

        ApiResponse apiResponse = new ApiResponse(true, "Drug retrieved successfully", drugResponse);
        return ResponseEntity.ok(apiResponse);
    }
}
