package com.medreceipt.model.enums;

/**
 * Enumeration representing the lifecycle status of a medical receipt.
 * <p>
 * Tracks the payment state of a receipt from creation through final settlement.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
public enum ReceiptStatus {

    /**
     * Receipt has been generated but payment has not yet been received.
     */
    PENDING,

    /**
     * The full amount on the receipt has been paid.
     */
    PAID,

    /**
     * A partial payment has been made against the receipt.
     */
    PARTIALLY_PAID,

    /**
     * The payment has been refunded to the patient.
     */
    REFUNDED,

    /**
     * The receipt has been cancelled and is no longer valid.
     */
    CANCELLED
}
