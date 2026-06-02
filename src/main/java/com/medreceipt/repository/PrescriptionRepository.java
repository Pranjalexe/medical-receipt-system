package com.medreceipt.repository;

import com.medreceipt.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Prescription} entity.
 * Provides CRUD operations and custom query methods for prescription management,
 * including eager-fetching queries for prescription items.
 *
 * @author MedReceipt
 * @since 1.0
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    /**
     * Finds all prescriptions issued by a specific doctor.
     *
     * @param doctorId the doctor's ID
     * @return a list of prescriptions issued by the doctor
     */
    List<Prescription> findByDoctorId(Long doctorId);

    /**
     * Finds all prescriptions for a specific patient.
     *
     * @param patientId the patient's ID
     * @return a list of prescriptions for the patient
     */
    List<Prescription> findByPatientId(Long patientId);

    /**
     * Finds all prescriptions issued by a specific doctor for a specific patient.
     *
     * @param doctorId  the doctor's ID
     * @param patientId the patient's ID
     * @return a list of prescriptions matching both doctor and patient
     */
    List<Prescription> findByDoctorIdAndPatientId(Long doctorId, Long patientId);

    /**
     * Fetches a prescription by ID with its items eagerly loaded using a JOIN FETCH.
     * This avoids the N+1 query problem when accessing prescription items.
     *
     * @param prescriptionId the prescription ID
     * @return an Optional containing the prescription with items if found, empty otherwise
     */
    @Query("SELECT p FROM Prescription p LEFT JOIN FETCH p.items WHERE p.id = :prescriptionId")
    Optional<Prescription> findByIdWithItems(@Param("prescriptionId") Long prescriptionId);

    /**
     * Fetches all prescriptions for a patient with their items eagerly loaded.
     * Uses DISTINCT to prevent duplicate results from the JOIN FETCH.
     *
     * @param patientId the patient's ID
     * @return a list of prescriptions with items eagerly fetched
     */
    @Query("SELECT DISTINCT p FROM Prescription p LEFT JOIN FETCH p.items WHERE p.patient.id = :patientId")
    List<Prescription> findByPatientIdWithItems(@Param("patientId") Long patientId);
}
