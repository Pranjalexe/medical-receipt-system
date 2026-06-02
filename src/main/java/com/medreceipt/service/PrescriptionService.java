package com.medreceipt.service;

import com.medreceipt.dto.request.PrescriptionItemRequest;
import com.medreceipt.dto.request.PrescriptionRequest;
import com.medreceipt.dto.response.PrescriptionItemResponse;
import com.medreceipt.dto.response.PrescriptionResponse;
import com.medreceipt.exception.ResourceNotFoundException;
import com.medreceipt.model.Doctor;
import com.medreceipt.model.Drug;
import com.medreceipt.model.Patient;
import com.medreceipt.model.Prescription;
import com.medreceipt.model.PrescriptionItem;
import com.medreceipt.repository.DoctorRepository;
import com.medreceipt.repository.DrugRepository;
import com.medreceipt.repository.PatientRepository;
import com.medreceipt.repository.PrescriptionItemRepository;
import com.medreceipt.repository.PrescriptionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing prescription operations.
 * <p>
 * Handles the full lifecycle of prescriptions including creation with drug verification,
 * retrieval by doctor or patient, and entity-to-DTO mapping. Drug verification is delegated
 * to {@link DrugVerificationService} which integrates with the OpenFDA API.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Service
public class PrescriptionService {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionService.class);

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DrugVerificationService drugVerificationService;
    private final DrugRepository drugRepository;

    /**
     * Constructs the PrescriptionService with all required dependencies.
     *
     * @param prescriptionRepository     repository for prescription persistence
     * @param prescriptionItemRepository repository for prescription item persistence
     * @param doctorRepository           repository for doctor lookups
     * @param patientRepository          repository for patient lookups
     * @param drugVerificationService    service for drug verification via OpenFDA
     * @param drugRepository             repository for drug lookups
     */
    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PrescriptionItemRepository prescriptionItemRepository,
                               DoctorRepository doctorRepository,
                               PatientRepository patientRepository,
                               DrugVerificationService drugVerificationService,
                               DrugRepository drugRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.drugVerificationService = drugVerificationService;
        this.drugRepository = drugRepository;
    }

    /**
     * Creates a new prescription for a patient by a doctor.
     * <p>
     * For each prescription item, the drug is either retrieved from the database by ID
     * or verified through the OpenFDA API by name. This ensures all prescribed drugs
     * are validated against the national drug code directory.
     * </p>
     *
     * @param doctorUserId the user ID of the prescribing doctor
     * @param request      the prescription creation request containing patient ID and items
     * @return a {@link PrescriptionResponse} containing the created prescription details
     * @throws ResourceNotFoundException if the doctor or patient is not found
     */
    @Transactional
    public PrescriptionResponse createPrescription(Long doctorUserId, PrescriptionRequest request) {
        log.info("Creating prescription: doctor user ID={}, patient ID={}", doctorUserId, request.getPatientId());

        // Get Doctor from user ID
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> {
                    log.error("Doctor not found for user ID: {}", doctorUserId);
                    return new ResourceNotFoundException("Doctor", "userId", doctorUserId);
                });

        // Get Patient by ID
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> {
                    log.error("Patient not found with ID: {}", request.getPatientId());
                    return new ResourceNotFoundException("Patient", "id", request.getPatientId());
                });

        // Create Prescription entity
        Prescription prescription = new Prescription();
        prescription.setDoctor(doctor);
        prescription.setPatient(patient);
        prescription.setNotes(request.getNotes());
        prescription.setIssueDate(java.time.LocalDate.now());

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.debug("Prescription entity saved with ID: {}", savedPrescription.getId());

        // Process each prescription item
        List<PrescriptionItem> items = new ArrayList<>();
        if (request.getItems() != null) {
            for (PrescriptionItemRequest itemRequest : request.getItems()) {
                PrescriptionItem item = new PrescriptionItem();
                item.setPrescription(savedPrescription);

                // Resolve the drug — by ID or by name via OpenFDA verification
                Drug drug = resolveDrug(itemRequest);
                item.setDrug(drug);

                item.setDosage(itemRequest.getDosage());
                item.setFrequency(itemRequest.getFrequency());
                item.setDuration(itemRequest.getDuration());
                item.setQuantity(itemRequest.getQuantity());

                PrescriptionItem savedItem = prescriptionItemRepository.save(item);
                items.add(savedItem);
                log.debug("Prescription item saved: drug='{}', dosage='{}'",
                        drug.getBrandName(), itemRequest.getDosage());
            }
        }

        savedPrescription.setPrescriptionItems(items);
        log.info("Prescription created successfully with ID: {} containing {} items",
                savedPrescription.getId(), items.size());

        return mapToResponse(savedPrescription);
    }

    /**
     * Retrieves all prescriptions created by a specific doctor.
     *
     * @param doctorUserId the user ID of the doctor
     * @return a list of {@link PrescriptionResponse} objects
     * @throws ResourceNotFoundException if the doctor is not found
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByDoctor(Long doctorUserId) {
        log.debug("Fetching prescriptions for doctor user ID: {}", doctorUserId);

        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", doctorUserId));

        List<Prescription> prescriptions = prescriptionRepository.findByDoctorId(doctor.getId());
        log.info("Found {} prescriptions for doctor ID: {}", prescriptions.size(), doctor.getId());

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all prescriptions for a specific patient.
     *
     * @param patientUserId the user ID of the patient
     * @return a list of {@link PrescriptionResponse} objects
     * @throws ResourceNotFoundException if the patient is not found
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByPatient(Long patientUserId) {
        log.debug("Fetching prescriptions for patient user ID: {}", patientUserId);

        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "userId", patientUserId));

        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patient.getId());
        log.info("Found {} prescriptions for patient ID: {}", prescriptions.size(), patient.getId());

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single prescription by its ID.
     *
     * @param id the prescription ID
     * @return a {@link PrescriptionResponse} with the prescription details
     * @throws ResourceNotFoundException if the prescription is not found
     */
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id) {
        log.debug("Fetching prescription by ID: {}", id);

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Prescription not found with ID: {}", id);
                    return new ResourceNotFoundException("Prescription", "id", id);
                });

        return mapToResponse(prescription);
    }

    /**
     * Resolves a {@link Drug} entity from a prescription item request.
     * <p>
     * If a drug ID is provided, the drug is looked up directly from the database.
     * If only a drug name is provided, the drug is verified against OpenFDA and cached locally.
     * </p>
     *
     * @param itemRequest the prescription item request
     * @return the resolved {@link Drug} entity
     * @throws ResourceNotFoundException if a drug ID is provided but not found
     */
    private Drug resolveDrug(PrescriptionItemRequest itemRequest) {
        if (itemRequest.getDrugId() != null) {
            log.debug("Resolving drug by ID: {}", itemRequest.getDrugId());
            return drugRepository.findById(itemRequest.getDrugId())
                    .orElseThrow(() -> {
                        log.error("Drug not found with ID: {}", itemRequest.getDrugId());
                        return new ResourceNotFoundException("Drug", "id", itemRequest.getDrugId());
                    });
        }

        if (itemRequest.getDrugName() != null && !itemRequest.getDrugName().isBlank()) {
            log.debug("Resolving drug by name via OpenFDA: '{}'", itemRequest.getDrugName());
            return drugVerificationService.getOrCreateDrug(itemRequest.getDrugName());
        }

        throw new IllegalArgumentException("Either drugId or drugName must be provided in prescription item");
    }

    /**
     * Maps a {@link Prescription} entity to a {@link PrescriptionResponse} DTO.
     *
     * @param prescription the prescription entity to map
     * @return the mapped {@link PrescriptionResponse}
     */
    private PrescriptionResponse mapToResponse(Prescription prescription) {
        PrescriptionResponse response = new PrescriptionResponse();
        response.setId(prescription.getId());
        response.setNotes(prescription.getNotes());
        response.setIssueDate(prescription.getIssueDate() != null ? prescription.getIssueDate().toString() : null);
        response.setCreatedAt(prescription.getCreatedAt() != null ? prescription.getCreatedAt().toString() : null);

        // Map doctor info
        if (prescription.getDoctor() != null) {
            if (prescription.getDoctor().getUser() != null) {
                response.setDoctorName(prescription.getDoctor().getUser().getFullName());
            }
        }

        // Map patient info
        if (prescription.getPatient() != null) {
            if (prescription.getPatient().getUser() != null) {
                response.setPatientName(prescription.getPatient().getUser().getFullName());
            }
        }

        // Map prescription items
        if (prescription.getPrescriptionItems() != null) {
            List<PrescriptionItemResponse> itemResponses = prescription.getPrescriptionItems().stream()
                    .map(this::mapItemToResponse)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        return response;
    }

    /**
     * Maps a {@link PrescriptionItem} entity to a {@link PrescriptionItemResponse} DTO.
     *
     * @param item the prescription item entity to map
     * @return the mapped {@link PrescriptionItemResponse}
     */
    private PrescriptionItemResponse mapItemToResponse(PrescriptionItem item) {
        PrescriptionItemResponse response = new PrescriptionItemResponse();
        response.setDosage(item.getDosage());
        response.setFrequency(item.getFrequency());
        response.setDuration(item.getDuration());
        response.setQuantity(item.getQuantity());

        if (item.getDrug() != null) {
            response.setDrugName(item.getDrug().getBrandName());
        }

        return response;
    }
}
