package com.medreceipt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a patient's profile in the system.
 * <p>
 * Each patient is linked to a {@link User} record that holds authentication
 * credentials and basic contact information. This entity captures
 * patient-specific medical data such as date of birth, address, and blood group.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Lob
    @Column(name = "patient_address", columnDefinition = "TEXT")
    private String address;

    @Size(max = 10, message = "Blood group must not exceed 10 characters")
    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Lifecycle Callbacks ──────────────────────────────────────────────

    /**
     * Sets the {@code createdAt} and {@code updatedAt} timestamps before
     * the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the {@code updatedAt} timestamp before the entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Constructors ─────────────────────────────────────────────────────

    /**
     * Default no-arg constructor required by JPA.
     */
    public Patient() {
    }

    /**
     * Constructs a new {@code Patient} with the essential fields.
     *
     * @param user        the associated user account
     * @param dateOfBirth the patient's date of birth
     * @param address     the patient's residential address
     * @param bloodGroup  the patient's blood group (e.g. "A+", "O-")
     */
    public Patient(User user, LocalDate dateOfBirth, String address, String bloodGroup) {
        this.user = user;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.bloodGroup = bloodGroup;
    }

    // ── Getters & Setters ────────────────────────────────────────────────

    /**
     * Returns the unique identifier of this patient profile.
     *
     * @return the patient ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this patient profile.
     *
     * @param id the patient ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the user account associated with this patient.
     *
     * @return the associated {@link User}
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user account associated with this patient.
     *
     * @param user the associated {@link User}
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the date of birth of this patient.
     *
     * @return the date of birth, or {@code null} if not set
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the date of birth of this patient.
     *
     * @param dateOfBirth the date of birth
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Returns the residential address of this patient.
     *
     * @return the address, or {@code null} if not set
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the residential address of this patient.
     *
     * @param address the residential address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the blood group of this patient.
     *
     * @return the blood group (e.g. "A+", "B-", "O+"), or {@code null} if not set
     */
    public String getBloodGroup() {
        return bloodGroup;
    }

    /**
     * Sets the blood group of this patient.
     *
     * @param bloodGroup the blood group
     */
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    /**
     * Returns the timestamp when this patient profile was created.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when this patient profile was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the timestamp when this patient profile was last updated.
     *
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when this patient profile was last updated.
     *
     * @param updatedAt the last update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ── Object Overrides ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", dateOfBirth=" + dateOfBirth +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
