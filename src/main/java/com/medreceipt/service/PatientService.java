package com.medreceipt.service;

import com.medreceipt.dto.request.RegisterRequest;
import com.medreceipt.exception.ResourceNotFoundException;
import com.medreceipt.model.Patient;
import com.medreceipt.model.User;
import com.medreceipt.repository.PatientRepository;
import com.medreceipt.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing patient-related operations.
 * <p>
 * Provides methods to retrieve patient profiles by user ID or patient ID,
 * and to update patient profile information such as address and phone number.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    /**
     * Constructs the PatientService with all required dependencies.
     *
     * @param patientRepository repository for patient persistence
     * @param userRepository    repository for user persistence
     */
    public PatientService(PatientRepository patientRepository,
                          UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a patient profile by the associated user ID.
     *
     * @param userId the user ID linked to the patient
     * @return the {@link Patient} entity
     * @throws ResourceNotFoundException if no patient is found for the given user ID
     */
    @Transactional(readOnly = true)
    public Patient getPatientByUserId(Long userId) {
        log.debug("Fetching patient profile for user ID: {}", userId);
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Patient not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Patient", "userId", userId);
                });
    }

    /**
     * Retrieves a patient by their primary key.
     *
     * @param patientId the patient's primary key
     * @return the {@link Patient} entity
     * @throws ResourceNotFoundException if no patient is found with the given ID
     */
    @Transactional(readOnly = true)
    public Patient getPatientById(Long patientId) {
        log.debug("Fetching patient by ID: {}", patientId);
        return patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Patient not found with ID: {}", patientId);
                    return new ResourceNotFoundException("Patient", "id", patientId);
                });
    }

    /**
     * Updates a patient's profile information.
     * <p>
     * Updates the patient's address, allergies, and blood group from the request,
     * as well as the associated user's phone number and full name.
     * </p>
     *
     * @param userId  the user ID of the patient to update
     * @param request the request containing the updated profile fields
     * @return the updated {@link Patient} entity
     * @throws ResourceNotFoundException if no patient is found for the given user ID
     */
    @Transactional
    public Patient updatePatient(Long userId, RegisterRequest request) {
        log.info("Updating patient profile for user ID: {}", userId);

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Patient not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Patient", "userId", userId);
                });

        // Update patient-specific fields
        if (request.getAddress() != null) {
            patient.setAddress(request.getAddress());
        }
        if (request.getBloodGroup() != null) {
            patient.setBloodGroup(request.getBloodGroup());
        }
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()) {
            patient.setDateOfBirth(java.time.LocalDate.parse(request.getDateOfBirth()));
        }

        // Update associated user fields
        User user = patient.getUser();
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        userRepository.save(user);

        Patient updatedPatient = patientRepository.save(patient);
        log.info("Patient profile updated successfully for user ID: {}", userId);

        return updatedPatient;
    }
}
