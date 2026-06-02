package com.medreceipt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Entity representing a single line item within a {@link Prescription}.
 * <p>
 * Each item captures the drug prescribed, along with its dosage instructions
 * (dosage amount, frequency, duration) and the quantity to be dispensed.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Entity
@Table(name = "prescription_items")
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Prescription reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @NotNull(message = "Drug reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Size(max = 100, message = "Dosage must not exceed 100 characters")
    @Column(name = "dosage", length = 100)
    private String dosage;

    @Size(max = 100, message = "Frequency must not exceed 100 characters")
    @Column(name = "frequency", length = 100)
    private String frequency;

    @Size(max = 100, message = "Duration must not exceed 100 characters")
    @Column(name = "duration", length = 100)
    private String duration;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity")
    private Integer quantity;

    // ── Constructors ─────────────────────────────────────────────────────

    /**
     * Default no-arg constructor required by JPA.
     */
    public PrescriptionItem() {
    }

    /**
     * Constructs a new {@code PrescriptionItem} with the essential fields.
     *
     * @param prescription the parent prescription
     * @param drug         the prescribed drug
     * @param dosage       the dosage amount (e.g. "500mg")
     * @param frequency    how often to take the drug (e.g. "twice daily")
     * @param duration     how long to take the drug (e.g. "7 days")
     * @param quantity     the number of units to dispense
     */
    public PrescriptionItem(Prescription prescription, Drug drug, String dosage,
                            String frequency, String duration, Integer quantity) {
        this.prescription = prescription;
        this.drug = drug;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.quantity = quantity;
    }

    // ── Getters & Setters ────────────────────────────────────────────────

    /**
     * Returns the unique identifier of this prescription item.
     *
     * @return the item ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this prescription item.
     *
     * @param id the item ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the parent prescription that this item belongs to.
     *
     * @return the parent {@link Prescription}
     */
    public Prescription getPrescription() {
        return prescription;
    }

    /**
     * Sets the parent prescription that this item belongs to.
     *
     * @param prescription the parent {@link Prescription}
     */
    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    /**
     * Returns the drug prescribed in this item.
     *
     * @return the {@link Drug}
     */
    public Drug getDrug() {
        return drug;
    }

    /**
     * Sets the drug prescribed in this item.
     *
     * @param drug the {@link Drug}
     */
    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    /**
     * Returns the dosage amount for this prescription item.
     *
     * @return the dosage (e.g. "500mg", "10ml"), or {@code null} if not specified
     */
    public String getDosage() {
        return dosage;
    }

    /**
     * Sets the dosage amount for this prescription item.
     *
     * @param dosage the dosage amount
     */
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    /**
     * Returns how often the drug should be taken.
     *
     * @return the frequency (e.g. "twice daily", "every 8 hours"), or {@code null}
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets how often the drug should be taken.
     *
     * @param frequency the frequency instruction
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * Returns the duration for which the drug should be taken.
     *
     * @return the duration (e.g. "7 days", "2 weeks"), or {@code null}
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the duration for which the drug should be taken.
     *
     * @param duration the duration instruction
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Returns the quantity of the drug to be dispensed.
     *
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the drug to be dispensed.
     *
     * @param quantity the quantity (must be at least 1)
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // ── Object Overrides ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionItem that = (PrescriptionItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PrescriptionItem{" +
                "id=" + id +
                ", dosage='" + dosage + '\'' +
                ", frequency='" + frequency + '\'' +
                ", duration='" + duration + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
