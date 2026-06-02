package com.medreceipt.dto.response;

/**
 * Data Transfer Object for individual prescription item responses.
 * Represents a single drug entry within a prescription with its dosage instructions.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class PrescriptionItemResponse {

    /**
     * The name of the prescribed drug.
     */
    private String drugName;

    /**
     * The dosage instructions (e.g., "500mg", "10ml").
     */
    private String dosage;

    /**
     * The frequency of administration (e.g., "Twice daily", "Every 8 hours").
     */
    private String frequency;

    /**
     * The duration for which the drug should be taken (e.g., "7 days", "2 weeks").
     */
    private String duration;

    /**
     * The total quantity of the drug to be dispensed.
     */
    private int quantity;

    /**
     * Default no-args constructor.
     */
    public PrescriptionItemResponse() {
    }

    /**
     * All-args constructor.
     *
     * @param drugName  the drug name
     * @param dosage    the dosage instructions
     * @param frequency the frequency of administration
     * @param duration  the duration
     * @param quantity  the quantity to dispense
     */
    public PrescriptionItemResponse(String drugName, String dosage, String frequency,
                                    String duration, int quantity) {
        this.drugName = drugName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.quantity = quantity;
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
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
