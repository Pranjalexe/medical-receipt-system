package com.medreceipt.repository;

import com.medreceipt.model.Receipt;
import com.medreceipt.model.enums.ReceiptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Receipt} entity.
 * Provides CRUD operations and custom query methods for receipt management,
 * including JPQL-based eager-fetching queries for receipt items.
 *
 * @author MedReceipt
 * @since 1.0
 */
@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    /**
     * Finds a receipt by its unique receipt number.
     *
     * @param receiptNumber the receipt number to search for
     * @return an Optional containing the receipt if found, empty otherwise
     */
    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    /**
     * Finds all receipts for a specific patient.
     *
     * @param patientId the patient's ID
     * @return a list of receipts for the patient
     */
    List<Receipt> findByPatientId(Long patientId);

    /**
     * Finds all receipts issued by a specific doctor.
     *
     * @param doctorId the doctor's ID
     * @return a list of receipts issued by the doctor
     */
    List<Receipt> findByDoctorId(Long doctorId);

    /**
     * Finds all receipts with a specific status.
     *
     * @param status the receipt status to filter by
     * @return a list of receipts with the specified status
     */
    List<Receipt> findByStatus(ReceiptStatus status);

    /**
     * Fetches all receipts for a patient with their items eagerly loaded using JPQL JOIN FETCH.
     * Uses DISTINCT to prevent duplicate results caused by the JOIN FETCH operation.
     *
     * @param patientId the patient's ID
     * @return a list of receipts with items eagerly fetched
     */
    @Query("SELECT DISTINCT r FROM Receipt r LEFT JOIN FETCH r.items WHERE r.patient.id = :patientId")
    List<Receipt> findByPatientIdWithItems(@Param("patientId") Long patientId);

    /**
     * Fetches all receipts issued by a doctor with their items eagerly loaded.
     * Uses DISTINCT to prevent duplicate results caused by the JOIN FETCH operation.
     *
     * @param doctorId the doctor's ID
     * @return a list of receipts with items eagerly fetched
     */
    @Query("SELECT DISTINCT r FROM Receipt r LEFT JOIN FETCH r.items WHERE r.doctor.id = :doctorId")
    List<Receipt> findByDoctorIdWithItems(@Param("doctorId") Long doctorId);

    /**
     * Fetches a single receipt by ID with its items eagerly loaded.
     *
     * @param receiptId the receipt ID
     * @return an Optional containing the receipt with items if found, empty otherwise
     */
    @Query("SELECT r FROM Receipt r LEFT JOIN FETCH r.items WHERE r.id = :receiptId")
    Optional<Receipt> findByIdWithItems(@Param("receiptId") Long receiptId);

    /**
     * Calculates the total revenue across all generated receipts.
     *
     * @return the sum of net amounts of all receipts
     */
    @Query("SELECT SUM(r.netAmount) FROM Receipt r")
    java.math.BigDecimal sumNetAmounts();
}
