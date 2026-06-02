package com.medreceipt.dto.response;

import java.math.BigDecimal;

/**
 * Data Transfer Object for individual receipt item responses.
 * Represents a single line item on a medical receipt with pricing details.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class ReceiptItemResponse {

    /**
     * The name of the drug on the receipt line item.
     */
    private String drugName;

    /**
     * The quantity of the drug dispensed.
     */
    private int quantity;

    /**
     * The price per unit at the time of sale.
     */
    private BigDecimal priceAtSale;

    /**
     * The subtotal for this line item (quantity × priceAtSale).
     */
    private BigDecimal subtotal;

    /**
     * Default no-args constructor.
     */
    public ReceiptItemResponse() {
    }

    /**
     * All-args constructor.
     *
     * @param drugName    the drug name
     * @param quantity    the quantity dispensed
     * @param priceAtSale the price per unit at time of sale
     * @param subtotal    the line item subtotal
     */
    public ReceiptItemResponse(String drugName, int quantity, BigDecimal priceAtSale, BigDecimal subtotal) {
        this.drugName = drugName;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
        this.subtotal = subtotal;
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

    /**
     * Gets the price at sale.
     *
     * @return the price at sale
     */
    public BigDecimal getPriceAtSale() {
        return priceAtSale;
    }

    /**
     * Sets the price at sale.
     *
     * @param priceAtSale the price at sale to set
     */
    public void setPriceAtSale(BigDecimal priceAtSale) {
        this.priceAtSale = priceAtSale;
    }

    /**
     * Gets the subtotal.
     *
     * @return the subtotal
     */
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal.
     *
     * @param subtotal the subtotal to set
     */
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
