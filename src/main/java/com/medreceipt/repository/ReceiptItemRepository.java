package com.medreceipt.repository;

import com.medreceipt.model.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link ReceiptItem} entity.
 * Provides CRUD operations and custom query methods for receipt item management.
 *
 * @author MedReceipt
 * @since 1.0
 */
@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {

    /**
     * Finds all receipt items belonging to a specific receipt.
     *
     * @param receiptId the receipt ID to search for
     * @return a list of receipt items for the given receipt
     */
    List<ReceiptItem> findByReceiptId(Long receiptId);
}
