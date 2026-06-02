package com.medreceipt.repository;

import com.medreceipt.model.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Drug} entity.
 * Provides CRUD operations and custom query methods for drug management,
 * including searches by NDC code, brand name, and generic name.
 *
 * @author MedReceipt
 * @since 1.0
 */
@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {

    /**
     * Finds a drug by its National Drug Code (NDC).
     *
     * @param ndcCode the NDC code to search for
     * @return an Optional containing the drug if found, empty otherwise
     */
    Optional<Drug> findByNdcCode(String ndcCode);

    /**
     * Finds drugs whose brand name contains the specified string (case-insensitive).
     *
     * @param brandName the brand name substring to search for
     * @return a list of drugs matching the brand name criteria
     */
    List<Drug> findByBrandNameContainingIgnoreCase(String brandName);

    /**
     * Finds drugs whose generic name contains the specified string (case-insensitive).
     *
     * @param genericName the generic name substring to search for
     * @return a list of drugs matching the generic name criteria
     */
    List<Drug> findByGenericNameContainingIgnoreCase(String genericName);

    /**
     * Finds a drug by its exact brand name (case-insensitive).
     *
     * @param brandName the exact brand name to search for
     * @return an Optional containing the drug if found, empty otherwise
     */
    Optional<Drug> findByBrandNameIgnoreCase(String brandName);
}
