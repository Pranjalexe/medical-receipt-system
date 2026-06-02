package com.medreceipt.model;

import com.medreceipt.model.enums.PaymentMethod;
import com.medreceipt.model.enums.ReceiptStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a medical receipt generated from a {@link Prescription}.
 * <p>
 * A receipt captures the financial details of a prescription fulfillment,
 * including individual line items ({@link ReceiptItem}), totals, discounts,
 * taxes, and payment information. The {@code @Version} annotation enables
 * optimistic locking to prevent concurrent modification conflicts.
 * </p>
 * <p>
 * Each receipt has a unique, indexed {@code receiptNumber} for quick lookups
 * and reference in printed/PDF documents.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Entity
@Table(name = "receipts", indexes = {
        @Index(name = "idx_receipt_receipt_number", columnList = "receipt_number", unique = true)
})
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Receipt number is required")
    @Size(max = 50, message = "Receipt number must not exceed 50 characters")
    @Column(name = "receipt_number", nullable = false, length = 50)
    private String receiptNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @NotNull(message = "Patient reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Doctor reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReceiptItem> items = new ArrayList<>();

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount", precision = 12, scale = 2, nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "net_amount", precision = 12, scale = 2)
    private BigDecimal netAmount;

    @NotNull(message = "Receipt status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_status", nullable = false, length = 20)
    private ReceiptStatus status = ReceiptStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Size(max = 500, message = "PDF path must not exceed 500 characters")
    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Version field for optimistic locking.
     * JPA will automatically increment this on each update and throw
     * {@code OptimisticLockException} on concurrent modification conflicts.
     */
    @Version
    @Column(name = "opt_version")
    private Long version;

    // ── Lifecycle Callbacks ──────────────────────────────────────────────

    /**
     * Sets the {@code generatedAt} and {@code updatedAt} timestamps before
     * the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
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
    public Receipt() {
    }

    /**
     * Constructs a new {@code Receipt} with the essential fields.
     *
     * @param receiptNumber the unique receipt number for identification
     * @param prescription  the associated prescription (may be {@code null})
     * @param patient       the patient being billed
     * @param doctor        the doctor who issued the prescription
     * @param totalAmount   the total amount before discount and tax
     * @param discount      the discount applied
     * @param taxAmount     the tax applied
     * @param netAmount     the final amount payable
     * @param status        the current payment status
     * @param paymentMethod the method of payment used
     */
    public Receipt(String receiptNumber, Prescription prescription, Patient patient,
                   Doctor doctor, BigDecimal totalAmount, BigDecimal discount,
                   BigDecimal taxAmount, BigDecimal netAmount, ReceiptStatus status,
                   PaymentMethod paymentMethod) {
        this.receiptNumber = receiptNumber;
        this.prescription = prescription;
        this.patient = patient;
        this.doctor = doctor;
        this.totalAmount = totalAmount;
        this.discount = discount != null ? discount : BigDecimal.ZERO;
        this.taxAmount = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        this.netAmount = netAmount;
        this.status = status != null ? status : ReceiptStatus.PENDING;
        this.paymentMethod = paymentMethod;
    }

    // ── Helper Methods ───────────────────────────────────────────────────

    /**
     * Adds a {@link ReceiptItem} to this receipt and sets the back-reference
     * on the item.
     *
     * @param item the receipt item to add
     */
    public void addItem(ReceiptItem item) {
        items.add(item);
        item.setReceipt(this);
    }

    /**
     * Removes a {@link ReceiptItem} from this receipt and clears the
     * back-reference on the item.
     *
     * @param item the receipt item to remove
     */
    public void removeItem(ReceiptItem item) {
        items.remove(item);
        item.setReceipt(null);
    }

    // ── Getters & Setters ────────────────────────────────────────────────

    /**
     * Returns the unique identifier of this receipt.
     *
     * @return the receipt ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this receipt.
     *
     * @param id the receipt ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the unique receipt number used for identification and display.
     *
     * @return the receipt number
     */
    public String getReceiptNumber() {
        return receiptNumber;
    }

    /**
     * Sets the unique receipt number.
     *
     * @param receiptNumber the receipt number
     */
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    /**
     * Returns the prescription associated with this receipt.
     *
     * @return the associated {@link Prescription}, or {@code null} if standalone
     */
    public Prescription getPrescription() {
        return prescription;
    }

    /**
     * Sets the prescription associated with this receipt.
     *
     * @param prescription the associated {@link Prescription}
     */
    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    /**
     * Returns the patient being billed on this receipt.
     *
     * @return the {@link Patient}
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the patient being billed on this receipt.
     *
     * @param patient the {@link Patient}
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Returns the doctor who issued the associated prescription.
     *
     * @return the {@link Doctor}
     */
    public Doctor getDoctor() {
        return doctor;
    }

    /**
     * Sets the doctor associated with this receipt.
     *
     * @param doctor the {@link Doctor}
     */
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * Returns the list of line items on this receipt.
     *
     * @return the list of {@link ReceiptItem}s
     */
    public List<ReceiptItem> getItems() {
        return items;
    }

    /**
     * Sets the list of line items on this receipt.
     *
     * @param items the list of {@link ReceiptItem}s
     */
    public void setItems(List<ReceiptItem> items) {
        this.items = items;
    }

    /**
     * Returns the total amount before discount and tax.
     *
     * @return the total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount before discount and tax.
     *
     * @param totalAmount the total amount
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Returns the discount applied to this receipt.
     *
     * @return the discount amount
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Sets the discount applied to this receipt.
     *
     * @param discount the discount amount
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * Returns the tax amount applied to this receipt.
     *
     * @return the tax amount
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * Sets the tax amount applied to this receipt.
     *
     * @param taxAmount the tax amount
     */
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    /**
     * Returns the net amount payable (total - discount + tax).
     *
     * @return the net amount
     */
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    /**
     * Sets the net amount payable.
     *
     * @param netAmount the net amount
     */
    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    /**
     * Returns the current payment status of this receipt.
     *
     * @return the {@link ReceiptStatus}
     */
    public ReceiptStatus getStatus() {
        return status;
    }

    /**
     * Sets the current payment status of this receipt.
     *
     * @param status the {@link ReceiptStatus}
     */
    public void setStatus(ReceiptStatus status) {
        this.status = status;
    }

    /**
     * Returns the payment method used for this receipt.
     *
     * @return the {@link PaymentMethod}, or {@code null} if not yet paid
     */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method used for this receipt.
     *
     * @param paymentMethod the {@link PaymentMethod}
     */
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Returns the file path to the generated PDF of this receipt.
     *
     * @return the PDF file path, or {@code null} if not yet generated
     */
    public String getPdfPath() {
        return pdfPath;
    }

    /**
     * Sets the file path to the generated PDF of this receipt.
     *
     * @param pdfPath the PDF file path
     */
    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    /**
     * Returns the timestamp when this receipt was generated.
     *
     * @return the generation timestamp
     */
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Sets the timestamp when this receipt was generated.
     *
     * @param generatedAt the generation timestamp
     */
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    /**
     * Returns the timestamp when this receipt was last updated.
     *
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when this receipt was last updated.
     *
     * @param updatedAt the last update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the optimistic locking version of this receipt.
     *
     * @return the version number
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the optimistic locking version of this receipt.
     * <p>
     * <strong>Note:</strong> This should generally not be set manually;
     * JPA manages the version field automatically.
     * </p>
     *
     * @param version the version number
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    // ── Object Overrides ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(id, receipt.id) && Objects.equals(receiptNumber, receipt.receiptNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiptNumber);
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", receiptNumber='" + receiptNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", netAmount=" + netAmount +
                ", status=" + status +
                ", paymentMethod=" + paymentMethod +
                ", generatedAt=" + generatedAt +
                ", version=" + version +
                '}';
    }
}
