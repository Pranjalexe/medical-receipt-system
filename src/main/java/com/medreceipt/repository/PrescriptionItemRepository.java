package com.medreceipt.repository;

import com.medreceipt.model.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link PrescriptionItem} entity.
 * Provides CRUD operations and custom query methods for prescription item management.
 *
 * @author MedReceipt
 * @since 1.0
 */
@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {

    /**
     * Finds all prescription items belonging to a specific prescription.
     *
     * @param prescriptionId the prescription ID to search for
     * @return a list of prescription items for the given prescription
     */
    List<PrescriptionItem> findByPrescriptionId(Long prescriptionId);
}
