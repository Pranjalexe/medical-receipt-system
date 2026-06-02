package com.medreceipt.model.enums;

/**
 * Enumeration representing the supported payment methods for medical receipts.
 * <p>
 * Defines the various channels through which a patient can settle a receipt.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
public enum PaymentMethod {

    /**
     * Payment made with physical cash.
     */
    CASH,

    /**
     * Payment made via credit or debit card.
     */
    CARD,

    /**
     * Payment covered by an insurance provider.
     */
    INSURANCE,

    /**
     * Payment made via Unified Payments Interface (UPI).
     */
    UPI,

    /**
     * Any other payment method not explicitly listed.
     */
    OTHER
}
