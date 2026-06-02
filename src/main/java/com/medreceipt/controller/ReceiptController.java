package com.medreceipt.controller;

import com.medreceipt.dto.response.ApiResponse;
import com.medreceipt.dto.response.ReceiptResponse;
import com.medreceipt.service.ReceiptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for general receipt operations.
 * <p>
 * Provides endpoints for retrieving receipts by ID or receipt number,
 * and updating receipt status. These endpoints are available to
 * authenticated users, with status updates restricted to doctors.
 * </p>
 *
 * @author MedReceipt
 * @version 1.0
 */
@RestController
@RequestMapping("/api/receipts")
@Tag(name = "Receipt", description = "General receipt management endpoints")
public class ReceiptController {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptController.class);

    private final ReceiptService receiptService;

    /**
     * Constructs a ReceiptController with the required ReceiptService dependency.
     *
     * @param receiptService the receipt service
     */
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    /**
     * Retrieves a receipt by its unique ID.
     * <p>
     * This endpoint is available to any authenticated user.
     * </p>
     *
     * @param id the receipt ID
     * @return ResponseEntity containing ApiResponse with the ReceiptResponse
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get receipt by ID", description = "Retrieves a specific receipt by its unique database identifier")
    public ResponseEntity<ApiResponse> getReceiptById(@PathVariable Long id) {
        logger.info("Fetching receipt [id={}]", id);

        ReceiptResponse receiptResponse = receiptService.getReceiptById(id);

        ApiResponse apiResponse = new ApiResponse(true, "Receipt retrieved successfully", receiptResponse);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Updates the status of a receipt.
     * <p>
     * Only users with the DOCTOR role are authorized to update receipt status.
     * Valid status values are defined by the ReceiptStatus enum (e.g., PENDING,
     * COMPLETED, CANCELLED).
     * </p>
     *
     * @param id     the receipt ID
     * @param status the new status to set
     * @return ResponseEntity containing ApiResponse with the updated ReceiptResponse
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Update receipt status", description = "Updates the status of a receipt (DOCTOR or ADMIN role required)")
    public ResponseEntity<ApiResponse> updateReceiptStatus(
            @PathVariable Long id,
            @RequestParam @Parameter(description = "New status for the receipt (e.g., PENDING, COMPLETED, CANCELLED)") String status) {

        logger.info("Updating receipt [id={}] status to: {}", id, status);

        ReceiptResponse receiptResponse = receiptService.updateReceiptStatus(id, status);

        logger.info("Receipt [id={}] status updated successfully to: {}", id, status);

        ApiResponse apiResponse = new ApiResponse(true, "Receipt status updated successfully", receiptResponse);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Finds a receipt by its unique receipt number.
     * <p>
     * Receipt numbers are human-readable identifiers (e.g., "REC-20240101-0001")
     * that can be used to look up receipts. Available to any authenticated user.
     * </p>
     *
     * @param receiptNumber the receipt number string
     * @return ResponseEntity containing ApiResponse with the ReceiptResponse
     */
    @GetMapping("/{receiptNumber}/by-number")
    @Operation(summary = "Find receipt by receipt number", description = "Retrieves a receipt using its human-readable receipt number")
    public ResponseEntity<ApiResponse> getReceiptByNumber(
            @PathVariable @Parameter(description = "The receipt number (e.g., REC-20240101-0001)") String receiptNumber) {

        logger.info("Fetching receipt by number: {}", receiptNumber);

        ReceiptResponse receiptResponse = receiptService.getReceiptByNumber(receiptNumber);

        ApiResponse apiResponse = new ApiResponse(true, "Receipt retrieved successfully", receiptResponse);
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Downloads the PDF version of a receipt.
     * <p>
     * This endpoint is available to any authenticated user (must be authorized for the specific receipt).
     * </p>
     *
     * @param id the receipt ID
     * @return ResponseEntity containing the PDF byte array
     */
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Download receipt PDF", description = "Downloads the generated PDF document for a specific receipt")
    public ResponseEntity<byte[]> downloadReceiptPdf(@PathVariable Long id) {
        logger.info("Downloading PDF for receipt [id={}]", id);

        byte[] pdfBytes = receiptService.getReceiptPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "receipt_" + id + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
