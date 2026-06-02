package com.medreceipt.repository;

import com.medreceipt.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link Patient} entity.
 * Provides CRUD operations and custom query methods for patient management.
 *
 * @author MedReceipt
 * @since 1.0
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds a patient by their associated user ID.
     *
     * @param userId the user ID to search for
     * @return an Optional containing the patient if found, empty otherwise
     */
    Optional<Patient> findByUserId(Long userId);
}
