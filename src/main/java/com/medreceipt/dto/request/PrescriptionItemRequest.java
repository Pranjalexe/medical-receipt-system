package com.medreceipt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for a single prescription item within a prescription request.
 * Represents a drug with its dosage instructions as prescribed by a doctor.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class PrescriptionItemRequest {

    /**
     * The ID of the drug being prescribed.
     * Either drugId or drugName should be provided for drug identification.
     */
    private Long drugId;

    /**
     * The name of the drug being prescribed.
     * Used as a fallback for drug lookup when drugId is not provided.
     */
    private String drugName;

    /**
     * The dosage instructions (e.g., "500mg", "10ml").
     */
    @NotBlank(message = "Dosage is required")
    private String dosage;

    /**
     * The frequency of administration (e.g., "Twice daily", "Every 8 hours").
     */
    @NotBlank(message = "Frequency is required")
    private String frequency;

    /**
     * The duration for which the drug should be taken (e.g., "7 days", "2 weeks").
     */
    private String duration;

    /**
     * The total quantity of the drug to be dispensed.
     */
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    /**
     * Default no-args constructor.
     */
    public PrescriptionItemRequest() {
    }

    /**
     * All-args constructor.
     *
     * @param drugId    the drug ID
     * @param drugName  the drug name for lookup
     * @param dosage    the dosage instructions
     * @param frequency the frequency of administration
     * @param duration  the duration of medication
     * @param quantity  the quantity to dispense
     */
    public PrescriptionItemRequest(Long drugId, String drugName, String dosage,
                                   String frequency, String duration, Integer quantity) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.quantity = quantity;
    }

    /**
     * Gets the drug ID.
     *
     * @return the drug ID
     */
    public Long getDrugId() {
        return drugId;
    }

    /**
     * Sets the drug ID.
     *
     * @param drugId the drug ID to set
     */
    public void setDrugId(Long drugId) {
        this.drugId = drugId;
    }

    /**
     * Gets the drug name.
     *
     * @return the drug name
     */
    public String getDrugName() {
        return drugName;
    }

    /**
     * Sets the drug name.
     *
     * @param drugName the drug name to set
     */
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    /**
     * Gets the dosage.
     *
     * @return the dosage
     */
    public String getDosage() {
        return dosage;
    }

    /**
     * Sets the dosage.
     *
     * @param dosage the dosage to set
     */
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    /**
     * Gets the frequency.
     *
     * @return the frequency
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency.
     *
     * @param frequency the frequency to set
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets the duration.
     *
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the duration.
     *
     * @param duration the duration to set
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
