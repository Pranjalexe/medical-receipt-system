package com.medreceipt.repository;

import com.medreceipt.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Doctor} entity.
 * Provides CRUD operations and custom query methods for doctor management.
 *
 * @author MedReceipt
 * @since 1.0
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Finds a doctor by their associated user ID.
     *
     * @param userId the user ID to search for
     * @return an Optional containing the doctor if found, empty otherwise
     */
    Optional<Doctor> findByUserId(Long userId);

    /**
     * Finds a doctor by their medical license number.
     *
     * @param licenseNumber the license number to search for
     * @return an Optional containing the doctor if found, empty otherwise
     */
    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    /**
     * Finds all doctors with a given specialization.
     *
     * @param specialization the specialization to filter by
     * @return a list of doctors with the specified specialization
     */
    List<Doctor> findBySpecialization(String specialization);
}
