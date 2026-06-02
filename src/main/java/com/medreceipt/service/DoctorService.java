package com.medreceipt.service;

import com.medreceipt.exception.ResourceNotFoundException;
import com.medreceipt.model.Doctor;
import com.medreceipt.model.Patient;
import com.medreceipt.repository.DoctorRepository;
import com.medreceipt.repository.PatientRepository;
import com.medreceipt.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing doctor-related operations.
 * <p>
 * Provides methods to retrieve doctor profiles by user ID and to list
 * patients associated with a given doctor. All read operations use
 * read-only transactions for optimal performance.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Service
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    /**
     * Constructs the DoctorService with all required dependencies.
     *
     * @param doctorRepository  repository for doctor persistence
     * @param userRepository    repository for user persistence
     * @param patientRepository repository for patient persistence
     */
    public DoctorService(DoctorRepository doctorRepository,
                         UserRepository userRepository,
                         PatientRepository patientRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Retrieves a doctor profile by the associated user ID.
     *
     * @param userId the user ID linked to the doctor
     * @return the {@link Doctor} entity
     * @throws ResourceNotFoundException if no doctor is found for the given user ID
     */
    @Transactional(readOnly = true)
    public Doctor getDoctorByUserId(Long userId) {
        log.debug("Fetching doctor profile for user ID: {}", userId);
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Doctor not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Doctor", "userId", userId);
                });
    }

    /**
     * Retrieves the doctor entity by its primary key.
     *
     * @param doctorId the doctor's primary key
     * @return the {@link Doctor} entity
     * @throws ResourceNotFoundException if no doctor is found with the given ID
     */
    @Transactional(readOnly = true)
    public Doctor getDoctorById(Long doctorId) {
        log.debug("Fetching doctor by ID: {}", doctorId);
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.error("Doctor not found with ID: {}", doctorId);
                    return new ResourceNotFoundException("Doctor", "id", doctorId);
                });
    }

    /**
     * Retrieves all patients in the system associated with a given doctor.
     * <p>
     * In a production application, this would filter patients through the prescriptions
     * relationship to return only those patients the doctor has treated. For simplicity,
     * this implementation returns all patients.
     * </p>
     *
     * @param doctorId the doctor's primary key
     * @return a list of {@link Patient} entities
     */
    @Transactional(readOnly = true)
    public List<Patient> getPatientsByDoctorId(Long doctorId) {
        log.debug("Fetching patients for doctor ID: {}", doctorId);

        // Verify the doctor exists
        if (!doctorRepository.existsById(doctorId)) {
            log.error("Doctor not found with ID: {}", doctorId);
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }

        // In a real-world application, this would query patients through the
        // prescriptions table to find only patients the doctor has treated.
        // For now, return all patients.
        List<Patient> patients = patientRepository.findAll();
        log.info("Found {} patients for doctor ID: {}", patients.size(), doctorId);
        return patients;
    }

    /**
     * Retrieves all doctors with a given specialization.
     *
     * @param specialization the medical specialization to filter by
     * @return a list of {@link Doctor} entities matching the specialization
     */
    @Transactional(readOnly = true)
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        log.debug("Fetching doctors with specialization: {}", specialization);
        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);
        log.info("Found {} doctors with specialization: {}", doctors.size(), specialization);
        return doctors;
    }
}
