package com.medreceipt.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a medical prescription issued by a doctor for a patient.
 * <p>
 * A prescription captures the doctor's orders, including a list of
 * {@link PrescriptionItem}s that detail the drugs, dosages, and frequencies.
 * The prescription serves as the basis for generating a {@link Receipt}.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Doctor reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NotNull(message = "Patient reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PrescriptionItem> prescriptionItems = new ArrayList<>();

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── Lifecycle Callbacks ──────────────────────────────────────────────

    /**
     * Sets the {@code createdAt} timestamp before the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ── Constructors ─────────────────────────────────────────────────────

    /**
     * Default no-arg constructor required by JPA.
     */
    public Prescription() {
    }

    /**
     * Constructs a new {@code Prescription} with the essential fields.
     *
     * @param doctor    the prescribing doctor
     * @param patient   the patient receiving the prescription
     * @param notes     any additional notes or instructions
     * @param issueDate the date the prescription was issued
     */
    public Prescription(Doctor doctor, Patient patient, String notes, LocalDate issueDate) {
        this.doctor = doctor;
        this.patient = patient;
        this.notes = notes;
        this.issueDate = issueDate;
    }

    // ── Helper Methods ───────────────────────────────────────────────────

    /**
     * Adds a {@link PrescriptionItem} to this prescription and sets the
     * back-reference on the item.
     *
     * @param item the prescription item to add
     */
    public void addPrescriptionItem(PrescriptionItem item) {
        prescriptionItems.add(item);
        item.setPrescription(this);
    }

    /**
     * Removes a {@link PrescriptionItem} from this prescription and clears
     * the back-reference on the item.
     *
     * @param item the prescription item to remove
     */
    public void removePrescriptionItem(PrescriptionItem item) {
        prescriptionItems.remove(item);
        item.setPrescription(null);
    }

    // ── Getters & Setters ────────────────────────────────────────────────

    /**
     * Returns the unique identifier of this prescription.
     *
     * @return the prescription ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this prescription.
     *
     * @param id the prescription ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the doctor who issued this prescription.
     *
     * @return the prescribing {@link Doctor}
     */
    public Doctor getDoctor() {
        return doctor;
    }

    /**
     * Sets the doctor who issued this prescription.
     *
     * @param doctor the prescribing {@link Doctor}
     */
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * Returns the patient for whom this prescription was issued.
     *
     * @return the {@link Patient}
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the patient for whom this prescription was issued.
     *
     * @param patient the {@link Patient}
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Returns the list of prescription items (drugs with dosage instructions).
     *
     * @return the list of {@link PrescriptionItem}s
     */
    public List<PrescriptionItem> getPrescriptionItems() {
        return prescriptionItems;
    }

    /**
     * Sets the list of prescription items.
     *
     * @param prescriptionItems the list of {@link PrescriptionItem}s
     */
    public void setPrescriptionItems(List<PrescriptionItem> prescriptionItems) {
        this.prescriptionItems = prescriptionItems;
    }

    /**
     * Returns any additional notes or instructions on this prescription.
     *
     * @return the notes, or {@code null} if none
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets additional notes or instructions on this prescription.
     *
     * @param notes the notes text
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the date this prescription was issued.
     *
     * @return the issue date
     */
    public LocalDate getIssueDate() {
        return issueDate;
    }

    /**
     * Sets the date this prescription was issued.
     *
     * @param issueDate the issue date
     */
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * Returns the timestamp when this prescription was created in the system.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when this prescription was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ── Object Overrides ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "id=" + id +
                ", issueDate=" + issueDate +
                ", createdAt=" + createdAt +
                ", itemCount=" + (prescriptionItems != null ? prescriptionItems.size() : 0) +
                '}';
    }
}
