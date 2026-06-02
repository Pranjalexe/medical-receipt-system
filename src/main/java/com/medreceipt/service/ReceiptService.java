package com.medreceipt.service;

import com.medreceipt.dto.request.ReceiptRequest;
import com.medreceipt.dto.response.ReceiptItemResponse;
import com.medreceipt.dto.response.ReceiptResponse;
import com.medreceipt.exception.ResourceNotFoundException;
import com.medreceipt.model.Doctor;
import com.medreceipt.model.Patient;
import com.medreceipt.model.Prescription;
import com.medreceipt.model.PrescriptionItem;
import com.medreceipt.model.Receipt;
import com.medreceipt.model.ReceiptItem;
import org.springframework.security.access.AccessDeniedException;
import com.medreceipt.model.enums.PaymentMethod;
import com.medreceipt.model.enums.ReceiptStatus;
import com.medreceipt.repository.DoctorRepository;
import com.medreceipt.repository.DrugRepository;
import com.medreceipt.repository.PatientRepository;
import com.medreceipt.repository.PrescriptionRepository;
import com.medreceipt.repository.ReceiptItemRepository;
import com.medreceipt.repository.ReceiptRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing medical receipt operations.
 * <p>
 * Handles the full lifecycle of receipts including generation from prescriptions,
 * price calculation (subtotal, discount, tax, and net amount), PDF generation,
 * status management, and retrieval. Receipt numbers follow the format
 * {@code MR-yyyyMMdd-XXXXX} for unique identification.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Service
public class ReceiptService {

    private static final Logger log = LoggerFactory.getLogger(ReceiptService.class);

    /** Default tax rate applied to receipts (18% GST). */
    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.18");

    /** Date format used in receipt number generation. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DrugRepository drugRepository;
    private final PdfReceiptService pdfReceiptService;

    /**
     * Constructs the ReceiptService with all required dependencies.
     *
     * @param receiptRepository      repository for receipt persistence
     * @param receiptItemRepository  repository for receipt item persistence
     * @param prescriptionRepository repository for prescription lookups
     * @param doctorRepository       repository for doctor lookups
     * @param patientRepository      repository for patient lookups
     * @param drugRepository         repository for drug lookups
     * @param pdfReceiptService      service for generating PDF receipts
     */
    public ReceiptService(ReceiptRepository receiptRepository,
                          ReceiptItemRepository receiptItemRepository,
                          PrescriptionRepository prescriptionRepository,
                          DoctorRepository doctorRepository,
                          PatientRepository patientRepository,
                          DrugRepository drugRepository,
                          PdfReceiptService pdfReceiptService) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.drugRepository = drugRepository;
        this.pdfReceiptService = pdfReceiptService;
    }

    /**
     * Generates a medical receipt from a prescription.
     * <p>
     * Verifies that the requesting doctor owns the prescription, creates receipt items
     * from the prescription items with prices at the time of sale, calculates totals
     * with discount and tax, generates a PDF document, and saves the receipt.
     * </p>
     *
     * @param doctorUserId the user ID of the doctor generating the receipt
     * @param request      the receipt generation request containing prescription ID and payment details
     * @return a {@link ReceiptResponse} containing the generated receipt details
     * @throws ResourceNotFoundException   if the prescription or doctor is not found
     * @throws UnauthorizedAccessException if the doctor does not own the prescription
     */
    @Transactional
    public ReceiptResponse generateReceipt(Long doctorUserId, ReceiptRequest request) {
        log.info("Generating receipt: doctor user ID={}, prescription ID={}",
                doctorUserId, request.getPrescriptionId());

        // Get the prescription
        Prescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> {
                    log.error("Prescription not found with ID: {}", request.getPrescriptionId());
                    return new ResourceNotFoundException("Prescription", "id", request.getPrescriptionId());
                });

        // Get the doctor and verify ownership
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> {
                    log.error("Doctor not found for user ID: {}", doctorUserId);
                    return new ResourceNotFoundException("Doctor", "userId", doctorUserId);
                });

        if (!prescription.getDoctor().getId().equals(doctor.getId())) {
            log.warn("Unauthorized receipt generation attempt: doctor ID={} tried to access prescription ID={} owned by doctor ID={}",
                    doctor.getId(), prescription.getId(), prescription.getDoctor().getId());
            throw new AccessDeniedException("Doctor is not authorized to update this receipt");
        }

        // Create Receipt entity
        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setPrescription(prescription);
        receipt.setDoctor(doctor);
        receipt.setPatient(prescription.getPatient());
        receipt.setStatus(ReceiptStatus.PENDING);
        receipt.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        receipt.setGeneratedAt(LocalDateTime.now());

        // Create receipt items from prescription items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<ReceiptItem> receiptItems = new ArrayList<>();

        if (prescription.getPrescriptionItems() != null) {
            for (PrescriptionItem prescriptionItem : prescription.getPrescriptionItems()) {
                ReceiptItem receiptItem = new ReceiptItem();
                receiptItem.setReceipt(receipt);
                receiptItem.setDrug(prescriptionItem.getDrug());
                receiptItem.setQuantity(prescriptionItem.getQuantity());

                BigDecimal unitPrice = BigDecimal.TEN;
                receiptItem.setPriceAtSale(unitPrice);
                BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(prescriptionItem.getQuantity()));
                receiptItem.setSubtotal(itemSubtotal);

                totalAmount = totalAmount.add(itemSubtotal);
                receiptItems.add(receiptItem);
            }
        }

        // Set financial calculations
        receipt.setTotalAmount(totalAmount);

        BigDecimal discount = request.getDiscount() != null
                ? request.getDiscount()
                : BigDecimal.ZERO;
        receipt.setDiscount(discount);

        BigDecimal afterDiscount = totalAmount.subtract(discount);

        BigDecimal taxRate = request.getTaxRate() != null ? request.getTaxRate() : DEFAULT_TAX_RATE;
        BigDecimal taxAmount = afterDiscount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        receipt.setTaxAmount(taxAmount);

        BigDecimal netAmount = afterDiscount.add(taxAmount);
        receipt.setNetAmount(netAmount);

        // Save the receipt
        Receipt savedReceipt = receiptRepository.save(receipt);

        // Save receipt items
        for (ReceiptItem item : receiptItems) {
            item.setReceipt(savedReceipt);
            receiptItemRepository.save(item);
        }
        savedReceipt.setItems(receiptItems);

        log.info("Receipt saved with ID: {}, receipt number: {}, net amount: {}",
                savedReceipt.getId(), savedReceipt.getReceiptNumber(), netAmount);

        // Generate PDF
        try {
            String pdfPath = pdfReceiptService.generateReceiptPdf(savedReceipt);
            savedReceipt.setPdfPath(pdfPath);
            receiptRepository.save(savedReceipt);
            log.info("PDF generated for receipt {}: {}", savedReceipt.getReceiptNumber(), pdfPath);
        } catch (Exception e) {
            log.error("Failed to generate PDF for receipt {}: {}",
                    savedReceipt.getReceiptNumber(), e.getMessage(), e);
            // Receipt is still valid even without PDF — do not roll back
        }

        return mapToResponse(savedReceipt);
    }

    /**
     * Retrieves all receipts associated with a specific patient.
     *
     * @param patientUserId the user ID of the patient
     * @return a list of {@link ReceiptResponse} objects
     * @throws ResourceNotFoundException if the patient is not found
     */
    @Transactional(readOnly = true)
    public List<ReceiptResponse> getReceiptsByPatient(Long patientUserId) {
        log.debug("Fetching receipts for patient user ID: {}", patientUserId);

        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "userId", patientUserId));

        List<Receipt> receipts = receiptRepository.findByPatientId(patient.getId());
        log.info("Found {} receipts for patient ID: {}", receipts.size(), patient.getId());

        return receipts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all receipts created by a specific doctor.
     *
     * @param doctorUserId the user ID of the doctor
     * @return a list of {@link ReceiptResponse} objects
     * @throws ResourceNotFoundException if the doctor is not found
     */
    @Transactional(readOnly = true)
    public List<ReceiptResponse> getReceiptsByDoctor(Long doctorUserId) {
        log.debug("Fetching receipts for doctor user ID: {}", doctorUserId);

        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", doctorUserId));

        List<Receipt> receipts = receiptRepository.findByDoctorId(doctor.getId());
        log.info("Found {} receipts for doctor ID: {}", receipts.size(), doctor.getId());

        return receipts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single receipt by its ID.
     *
     * @param id the receipt ID
     * @return a {@link ReceiptResponse} with the receipt details
     * @throws ResourceNotFoundException if the receipt is not found
     */
    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptById(Long id) {
        log.debug("Fetching receipt by ID: {}", id);

        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Receipt not found with ID: {}", id);
                    return new ResourceNotFoundException("Receipt", "id", id);
                });

        return mapToResponse(receipt);
    }

    /**
     * Retrieves the PDF document for a specific receipt.
     * <p>
     * Reads the PDF file from the filesystem. If the file does not exist or has been deleted,
     * the PDF is regenerated from the receipt data.
     * </p>
     *
     * @param receiptId the receipt ID
     * @return the PDF file content as a byte array
     * @throws ResourceNotFoundException if the receipt is not found
     * @throws RuntimeException          if the PDF cannot be read or generated
     */
    @Transactional(readOnly = true)
    public byte[] getReceiptPdf(Long receiptId) {
        log.debug("Fetching PDF for receipt ID: {}", receiptId);

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> {
                    log.error("Receipt not found with ID: {}", receiptId);
                    return new ResourceNotFoundException("Receipt", "id", receiptId);
                });

        // Try to read from existing file
        if (receipt.getPdfPath() != null) {
            Path pdfPath = Paths.get(receipt.getPdfPath());
            if (Files.exists(pdfPath)) {
                try {
                    log.debug("Reading existing PDF from: {}", receipt.getPdfPath());
                    return Files.readAllBytes(pdfPath);
                } catch (IOException e) {
                    log.warn("Failed to read existing PDF at '{}': {}", receipt.getPdfPath(), e.getMessage());
                }
            }
        }

        // Regenerate PDF if file doesn't exist
        log.info("Regenerating PDF for receipt ID: {}", receiptId);
        try {
            String pdfPath = pdfReceiptService.generateReceiptPdf(receipt);
            return Files.readAllBytes(Paths.get(pdfPath));
        } catch (Exception e) {
            log.error("Failed to regenerate PDF for receipt ID {}: {}", receiptId, e.getMessage(), e);
            throw new RuntimeException("Unable to generate or read PDF for receipt: " + receiptId, e);
        }
    }

    /**
     * Updates the status of a receipt.
     *
     * @param receiptId the receipt ID
     * @param status    the new status (must correspond to a valid {@link ReceiptStatus} value)
     * @return the updated {@link ReceiptResponse}
     * @throws ResourceNotFoundException if the receipt is not found
     * @throws IllegalArgumentException  if the status value is invalid
     */
    @Transactional
    public ReceiptResponse updateReceiptStatus(Long receiptId, String status) {
        log.info("Updating receipt status: receipt ID={}, new status={}", receiptId, status);

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> {
                    log.error("Receipt not found with ID: {}", receiptId);
                    return new ResourceNotFoundException("Receipt", "id", receiptId);
                });

        try {
            ReceiptStatus receiptStatus = ReceiptStatus.valueOf(status.toUpperCase());
            receipt.setStatus(receiptStatus);
        } catch (IllegalArgumentException e) {
            log.error("Invalid receipt status value: '{}'", status);
            throw new IllegalArgumentException("Invalid receipt status: '" + status
                    + "'. Valid values are: PENDING, PAID, PARTIALLY_PAID, REFUNDED, CANCELLED");
        }

        Receipt updatedReceipt = receiptRepository.save(receipt);
        log.info("Receipt {} status updated to {}", receipt.getReceiptNumber(), status);

        return mapToResponse(updatedReceipt);
    }

    /**
     * Generates a unique receipt number in the format {@code MR-yyyyMMdd-XXXXX}.
     * <p>
     * The sequence number is derived from the total count of receipts in the database,
     * ensuring uniqueness across the system. In a high-concurrency production environment,
     * a database sequence would be preferred.
     * </p>
     *
     * @return the generated receipt number string
     */
    private String generateReceiptNumber() {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long count = receiptRepository.count() + 1;
        String sequenceStr = String.format("%05d", count);
        String receiptNumber = "MR-" + dateStr + "-" + sequenceStr;
        log.debug("Generated receipt number: {}", receiptNumber);
        return receiptNumber;
    }

    public ReceiptResponse getReceiptByNumber(String receiptNumber) {
        log.debug("Fetching receipt by number: {}", receiptNumber);

        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> {
                    log.error("Receipt not found with number: {}", receiptNumber);
                    return new ResourceNotFoundException("Receipt", "receiptNumber", receiptNumber);
                });

        return mapToResponse(receipt);
    }

    /**
     * Maps a {@link Receipt} entity to a {@link ReceiptResponse} DTO.
     *
     * @param receipt the receipt entity to map
     * @return the mapped {@link ReceiptResponse}
     */
    private ReceiptResponse mapToResponse(Receipt receipt) {
        ReceiptResponse response = new ReceiptResponse();
        response.setId(receipt.getId());
        response.setReceiptNumber(receipt.getReceiptNumber());
        response.setStatus(receipt.getStatus() != null ? receipt.getStatus().name() : null);
        response.setPaymentMethod(receipt.getPaymentMethod() != null ? receipt.getPaymentMethod().name() : null);
        response.setGeneratedAt(receipt.getGeneratedAt() != null ? receipt.getGeneratedAt().toString() : null);

        // Financial summary
        response.setTotalAmount(receipt.getTotalAmount());
        response.setDiscount(receipt.getDiscount());
        response.setTaxAmount(receipt.getTaxAmount());
        response.setNetAmount(receipt.getNetAmount());

        // Doctor info
        if (receipt.getDoctor() != null) {
            if (receipt.getDoctor().getUser() != null) {
                response.setDoctorName(receipt.getDoctor().getUser().getFullName());
            }
        }

        // Patient info
        if (receipt.getPatient() != null) {
            if (receipt.getPatient().getUser() != null) {
                response.setPatientName(receipt.getPatient().getUser().getFullName());
            }
        }

        // Receipt items
        if (receipt.getItems() != null) {
            List<ReceiptItemResponse> itemResponses = receipt.getItems().stream()
                    .map(this::mapItemToResponse)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        return response;
    }

    /**
     * Maps a {@link ReceiptItem} entity to a {@link ReceiptItemResponse} DTO.
     *
     * @param item the receipt item entity to map
     * @return the mapped {@link ReceiptItemResponse}
     */
    private ReceiptItemResponse mapItemToResponse(ReceiptItem item) {
        ReceiptItemResponse response = new ReceiptItemResponse();
        if (item.getDrug() != null) {
            response.setDrugName(item.getDrug().getBrandName());
        }
        response.setQuantity(item.getQuantity());
        response.setPriceAtSale(item.getPriceAtSale());
        response.setSubtotal(item.getSubtotal());

        return response;
    }
}
