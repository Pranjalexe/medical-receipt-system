package com.medreceipt.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for receipt responses.
 * Contains all details of a generated medical receipt including line items
 * and financial summary.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class ReceiptResponse {

    /**
     * The unique database ID of the receipt.
     */
    private Long id;

    /**
     * The human-readable unique receipt number (e.g., "REC-20260602-001").
     */
    private String receiptNumber;

    /**
     * The full name of the patient.
     */
    private String patientName;

    /**
     * The full name of the prescribing doctor.
     */
    private String doctorName;

    /**
     * The list of items included in the receipt.
     */
    private List<ReceiptItemResponse> items;

    /**
     * The total amount before discount and tax.
     */
    private BigDecimal totalAmount;

    /**
     * The discount amount applied to the receipt.
     */
    private BigDecimal discount;

    /**
     * The tax amount applied to the receipt.
     */
    private BigDecimal taxAmount;

    /**
     * The net amount payable after discount and tax adjustments.
     */
    private BigDecimal netAmount;

    /**
     * The current status of the receipt (e.g., "GENERATED", "PAID", "CANCELLED").
     */
    private String status;

    /**
     * The method of payment used (e.g., "CASH", "CARD", "UPI").
     */
    private String paymentMethod;

    /**
     * The timestamp when the receipt was generated, formatted as a string.
     */
    private String generatedAt;

    /**
     * Default no-args constructor.
     */
    public ReceiptResponse() {
    }

    /**
     * Gets the receipt ID.
     *
     * @return the receipt ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the receipt ID.
     *
     * @param id the receipt ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the receipt number.
     *
     * @return the receipt number
     */
    public String getReceiptNumber() {
        return receiptNumber;
    }

    /**
     * Sets the receipt number.
     *
     * @param receiptNumber the receipt number to set
     */
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    /**
     * Gets the patient name.
     *
     * @return the patient name
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * Sets the patient name.
     *
     * @param patientName the patient name to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * Gets the doctor name.
     *
     * @return the doctor name
     */
    public String getDoctorName() {
        return doctorName;
    }

    /**
     * Sets the doctor name.
     *
     * @param doctorName the doctor name to set
     */
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    /**
     * Gets the list of receipt items.
     *
     * @return the list of receipt item responses
     */
    public List<ReceiptItemResponse> getItems() {
        return items;
    }

    /**
     * Sets the list of receipt items.
     *
     * @param items the list of receipt item responses to set
     */
    public void setItems(List<ReceiptItemResponse> items) {
        this.items = items;
    }

    /**
     * Gets the total amount.
     *
     * @return the total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount.
     *
     * @param totalAmount the total amount to set
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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
     * Gets the tax amount.
     *
     * @return the tax amount
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * Sets the tax amount.
     *
     * @param taxAmount the tax amount to set
     */
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    /**
     * Gets the net amount.
     *
     * @return the net amount
     */
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    /**
     * Sets the net amount.
     *
     * @param netAmount the net amount to set
     */
    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
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

    /**
     * Gets the generated-at timestamp.
     *
     * @return the generated-at timestamp as a string
     */
    public String getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Sets the generated-at timestamp.
     *
     * @param generatedAt the generated-at timestamp to set
     */
    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}
