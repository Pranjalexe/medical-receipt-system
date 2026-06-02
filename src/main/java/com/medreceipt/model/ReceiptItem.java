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

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity representing a single line item on a {@link Receipt}.
 * <p>
 * Each item captures a drug sold, along with the quantity dispensed, the unit
 * price at the time of sale ({@code priceAtSale}), and the computed subtotal.
 * The price is recorded at sale time to maintain an accurate historical record
 * even if the drug's price changes later.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Entity
@Table(name = "receipt_items")
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Receipt reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @NotNull(message = "Drug reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull(message = "Price at sale is required")
    @Column(name = "price_at_sale", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtSale;

    @NotNull(message = "Subtotal is required")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    // ── Constructors ─────────────────────────────────────────────────────

    /**
     * Default no-arg constructor required by JPA.
     */
    public ReceiptItem() {
    }

    /**
     * Constructs a new {@code ReceiptItem} with the essential fields.
     *
     * @param receipt     the parent receipt
     * @param drug        the drug being sold
     * @param quantity    the number of units dispensed
     * @param priceAtSale the unit price at the time of sale
     * @param subtotal    the total for this line item (quantity × priceAtSale)
     */
    public ReceiptItem(Receipt receipt, Drug drug, Integer quantity,
                       BigDecimal priceAtSale, BigDecimal subtotal) {
        this.receipt = receipt;
        this.drug = drug;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
        this.subtotal = subtotal;
    }

    // ── Getters & Setters ────────────────────────────────────────────────

    /**
     * Returns the unique identifier of this receipt item.
     *
     * @return the item ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this receipt item.
     *
     * @param id the item ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the parent receipt that this item belongs to.
     *
     * @return the parent {@link Receipt}
     */
    public Receipt getReceipt() {
        return receipt;
    }

    /**
     * Sets the parent receipt that this item belongs to.
     *
     * @param receipt the parent {@link Receipt}
     */
    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    /**
     * Returns the drug sold in this line item.
     *
     * @return the {@link Drug}
     */
    public Drug getDrug() {
        return drug;
    }

    /**
     * Sets the drug sold in this line item.
     *
     * @param drug the {@link Drug}
     */
    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    /**
     * Returns the quantity of the drug dispensed.
     *
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the drug dispensed.
     *
     * @param quantity the quantity (must be at least 1)
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the unit price of the drug at the time of sale.
     * <p>
     * This captures the historical price so the receipt remains accurate
     * even if the drug's current price changes.
     * </p>
     *
     * @return the price at sale
     */
    public BigDecimal getPriceAtSale() {
        return priceAtSale;
    }

    /**
     * Sets the unit price of the drug at the time of sale.
     *
     * @param priceAtSale the price at sale
     */
    public void setPriceAtSale(BigDecimal priceAtSale) {
        this.priceAtSale = priceAtSale;
    }

    /**
     * Returns the subtotal for this line item (quantity × priceAtSale).
     *
     * @return the subtotal
     */
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal for this line item.
     *
     * @param subtotal the subtotal
     */
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    // ── Object Overrides ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceiptItem that = (ReceiptItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ReceiptItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", priceAtSale=" + priceAtSale +
                ", subtotal=" + subtotal +
                '}';
    }
}
