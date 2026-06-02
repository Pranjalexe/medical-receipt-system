package com.medreceipt.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Data Transfer Object for creating a new receipt from an existing prescription.
 * Contains the prescription reference along with financial details like discount and tax.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class ReceiptRequest {

    /**
     * The ID of the prescription from which this receipt is generated.
     */
    @NotNull(message = "Prescription ID is required")
    private Long prescriptionId;

    /**
     * Optional discount amount to apply to the receipt total.
     */
    private BigDecimal discount;

    /**
     * Optional tax rate to apply (as a percentage, e.g., 18.00 for 18%).
     */
    private BigDecimal taxRate;

    /**
     * The method of payment (e.g., "CASH", "CARD", "UPI", "INSURANCE").
     */
    private String paymentMethod;

    /**
     * Default no-args constructor.
     */
    public ReceiptRequest() {
    }

    /**
     * All-args constructor.
     *
     * @param prescriptionId the prescription ID
     * @param discount       the discount amount
     * @param taxRate        the tax rate percentage
     * @param paymentMethod  the payment method
     */
    public ReceiptRequest(Long prescriptionId, BigDecimal discount, BigDecimal taxRate, String paymentMethod) {
        this.prescriptionId = prescriptionId;
        this.discount = discount;
        this.taxRate = taxRate;
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the prescription ID.
     *
     * @return the prescription ID
     */
    public Long getPrescriptionId() {
        return prescriptionId;
    }

    /**
     * Sets the prescription ID.
     *
     * @param prescriptionId the prescription ID to set
     */
    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    /**
     * Gets the discount amount.
     *
     * @return the discount amount
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Sets the discount amount.
     *
     * @param discount the discount amount to set
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * Gets the tax rate.
     *
     * @return the tax rate percentage
     */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /**
     * Sets the tax rate.
     *
     * @param taxRate the tax rate percentage to set
     */
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /**
     * Gets the payment method.
     *
     * @return the payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the payment method to set
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
