package com.medreceipt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a drug/medication record cached from the OpenFDA API.
 * <p>
 * Drug data is fetched from the OpenFDA drug labeling endpoint and cached locally
 * to reduce external API calls. The {@code verified} flag indicates whether the
 * drug data has been reviewed and confirmed by an administrator.
 * </p>
 * <p>
 * Indexes are defined on {@code ndc_code} and {@code brand_name} for efficient
 * lookups during prescription creation and drug searches.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Entity
@Table(name = "drugs", indexes = {
        @Index(name = "idx_drug_ndc_code", columnList = "ndc_code"),
        @Index(name = "idx_drug_brand_name", columnList = "brand_name")
})
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Brand name is required")
    @Size(max = 300, message = "Brand name must not exceed 300 characters")
    @Column(name = "brand_name", nullable = false, length = 300)
    private String brandName;

    @Size(max = 300, message = "Generic name must not exceed 300 characters")
    @Column(name = "generic_name", length = 300)
    private String genericName;

    @Size(max = 100, message = "NDC code must not exceed 100 characters")
    @Column(name = "ndc_code", length = 100)
    private String ndcCode;

    @Size(max = 300, message = "Manufacturer must not exceed 300 characters")
    @Column(name = "manufacturer", length = 300)
    private String manufacturer;

    @Size(max = 100, message = "Dosage form must not exceed 100 characters")
    @Column(name = "dosage_form", length = 100)
    private String dosageForm;

    @Size(max = 100, message = "Route must not exceed 100 characters")
    @Column(name = "admin_route", length = 100)
    private String route;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "cached_at", nullable = false, updatable = false)
    private LocalDateTime cachedAt;

    // ── Lifecycle Callbacks ──────────────────────────────────────────────

    /**
     * Sets the {@code cachedAt} timestamp before the entity is first persisted.
     */
    @PrePersist
    protected void onCache() {
        this.cachedAt = LocalDateTime.now();
    }

    // ── Constructors ─────────────────────────────────────────────────────

    /**
     * Default no-arg constructor required by JPA.
     */
    public Drug() {
    }

    /**
     * Constructs a new {@code Drug} with the commonly used fields.
     *
     * @param brandName    the brand/trade name of the drug
     * @param genericName  the generic/scientific name of the drug
     * @param ndcCode      the National Drug Code
     * @param manufacturer the manufacturer or labeler name
     * @param dosageForm   the dosage form (e.g. "TABLET", "CAPSULE")
     * @param route        the administration route (e.g. "ORAL", "TOPICAL")
     */
    public Drug(String brandName, String genericName, String ndcCode,
                String manufacturer, String dosageForm, String route) {
        this.brandName = brandName;
        this.genericName = genericName;
        this.ndcCode = ndcCode;
        this.manufacturer = manufacturer;
        this.dosageForm = dosageForm;
        this.route = route;
    }

    // ── Getters & Setters ────────────────────────────────────────────────

    /**
     * Returns the unique identifier of this drug record.
     *
     * @return the drug ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this drug record.
     *
     * @param id the drug ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the brand (trade) name of this drug.
     *
     * @return the brand name
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * Sets the brand (trade) name of this drug.
     *
     * @param brandName the brand name
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * Returns the generic (scientific) name of this drug.
     *
     * @return the generic name, or {@code null} if not available
     */
    public String getGenericName() {
        return genericName;
    }

    /**
     * Sets the generic (scientific) name of this drug.
     *
     * @param genericName the generic name
     */
    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    /**
     * Returns the National Drug Code (NDC) of this drug.
     *
     * @return the NDC code, or {@code null} if not available
     */
    public String getNdcCode() {
        return ndcCode;
    }

    /**
     * Sets the National Drug Code (NDC) of this drug.
     *
     * @param ndcCode the NDC code
     */
    public void setNdcCode(String ndcCode) {
        this.ndcCode = ndcCode;
    }

    /**
     * Returns the manufacturer or labeler of this drug.
     *
     * @return the manufacturer name, or {@code null} if not available
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the manufacturer or labeler of this drug.
     *
     * @param manufacturer the manufacturer name
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Returns the dosage form of this drug (e.g. TABLET, CAPSULE, INJECTION).
     *
     * @return the dosage form, or {@code null} if not available
     */
    public String getDosageForm() {
        return dosageForm;
    }

    /**
     * Sets the dosage form of this drug.
     *
     * @param dosageForm the dosage form
     */
    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    /**
     * Returns the administration route of this drug (e.g. ORAL, TOPICAL).
     *
     * @return the route, or {@code null} if not available
     */
    public String getRoute() {
        return route;
    }

    /**
     * Sets the administration route of this drug.
     *
     * @param route the administration route
     */
    public void setRoute(String route) {
        this.route = route;
    }

    /**
     * Returns whether this drug has been verified by an administrator.
     *
     * @return {@code true} if verified, {@code false} otherwise
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * Sets the verification status of this drug.
     *
     * @param verified {@code true} to mark as verified
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    /**
     * Returns the timestamp when this drug data was cached from the OpenFDA API.
     *
     * @return the cache timestamp
     */
    public LocalDateTime getCachedAt() {
        return cachedAt;
    }

    /**
     * Sets the timestamp when this drug data was cached.
     *
     * @param cachedAt the cache timestamp
     */
    public void setCachedAt(LocalDateTime cachedAt) {
        this.cachedAt = cachedAt;
    }

    // ── Object Overrides ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drug drug = (Drug) o;
        return Objects.equals(id, drug.id) && Objects.equals(ndcCode, drug.ndcCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ndcCode);
    }

    @Override
    public String toString() {
        return "Drug{" +
                "id=" + id +
                ", brandName='" + brandName + '\'' +
                ", genericName='" + genericName + '\'' +
                ", ndcCode='" + ndcCode + '\'' +
                ", verified=" + verified +
                ", cachedAt=" + cachedAt +
                '}';
    }
}
