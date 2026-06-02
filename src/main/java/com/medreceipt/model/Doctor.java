package com.medreceipt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a doctor's professional profile in the system.
 * <p>
 * Each doctor is linked to a {@link User} record that holds authentication
 * credentials and basic contact information. This entity captures
 * medical-specific data such as specialization and license number.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 150, message = "Specialization must not exceed 150 characters")
    @Column(name = "specialization", length = 150)
    private String specialization;

    @NotBlank(message = "License number is required")
    @Size(max = 100, message = "License number must not exceed 100 characters")
    @Column(name = "license_number", nullable = false, unique = true, length = 100)
    private String licenseNumber;

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
    public Doctor() {
    }

    /**
     * Constructs a new {@code Doctor} with the essential fields.
     *
     * @param user           the associated user account
     * @param specialization the doctor's medical specialization (e.g. Cardiology)
     * @param licenseNumber  the doctor's unique medical license number
     */
    public Doctor(User user, String specialization, String licenseNumber) {
        this.user = user;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
    }

    // ── Getters & Setters ────────────────────────────────────────────────

    /**
     * Returns the unique identifier of this doctor profile.
     *
     * @return the doctor ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this doctor profile.
     *
     * @param id the doctor ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the user account associated with this doctor.
     *
     * @return the associated {@link User}
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user account associated with this doctor.
     *
     * @param user the associated {@link User}
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the medical specialization of this doctor.
     *
     * @return the specialization, or {@code null} if not set
     */
    public String getSpecialization() {
        return specialization;
    }

    /**
     * Sets the medical specialization of this doctor.
     *
     * @param specialization the specialization (e.g. "Cardiology", "Dermatology")
     */
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    /**
     * Returns the medical license number of this doctor.
     *
     * @return the license number
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Sets the medical license number of this doctor.
     *
     * @param licenseNumber the unique license number
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * Returns the timestamp when this doctor profile was created.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when this doctor profile was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the timestamp when this doctor profile was last updated.
     *
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when this doctor profile was last updated.
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
        Doctor doctor = (Doctor) o;
        return Objects.equals(id, doctor.id) && Objects.equals(licenseNumber, doctor.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, licenseNumber);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", specialization='" + specialization + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
