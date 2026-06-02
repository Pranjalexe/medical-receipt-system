package com.medreceipt.dto.response;

/**
 * Data Transfer Object for drug verification responses.
 * Contains details about a verified drug, including its identification data
 * and the source of verification (OpenFDA API or local cache).
 *
 * @author MedReceipt
 * @since 1.0
 */
public class DrugVerificationResponse {

    /**
     * The brand (trade) name of the drug.
     */
    private String brandName;

    /**
     * The generic (active ingredient) name of the drug.
     */
    private String genericName;

    /**
     * The National Drug Code (NDC) identifying the drug.
     */
    private String ndcCode;

    /**
     * The manufacturer or labeler of the drug.
     */
    private String manufacturer;

    /**
     * The dosage form of the drug (e.g., "TABLET", "CAPSULE", "INJECTION").
     */
    private String dosageForm;

    /**
     * The route of administration (e.g., "ORAL", "INTRAVENOUS", "TOPICAL").
     */
    private String route;

    /**
     * Whether the drug was successfully verified against a known database.
     */
    private boolean verified;

    /**
     * The source of the verification data ("OpenFDA" or "Cache").
     */
    private String source;

    /**
     * Default no-args constructor.
     */
    public DrugVerificationResponse() {
    }

    /**
     * All-args constructor.
     *
     * @param brandName    the brand name
     * @param genericName  the generic name
     * @param ndcCode      the NDC code
     * @param manufacturer the manufacturer
     * @param dosageForm   the dosage form
     * @param route        the route of administration
     * @param verified     whether the drug is verified
     * @param source       the source of verification
     */
    public DrugVerificationResponse(String brandName, String genericName, String ndcCode,
                                    String manufacturer, String dosageForm, String route,
                                    boolean verified, String source) {
        this.brandName = brandName;
        this.genericName = genericName;
        this.ndcCode = ndcCode;
        this.manufacturer = manufacturer;
        this.dosageForm = dosageForm;
        this.route = route;
        this.verified = verified;
        this.source = source;
    }

    /**
     * Gets the brand name.
     *
     * @return the brand name
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * Sets the brand name.
     *
     * @param brandName the brand name to set
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * Gets the generic name.
     *
     * @return the generic name
     */
    public String getGenericName() {
        return genericName;
    }

    /**
     * Sets the generic name.
     *
     * @param genericName the generic name to set
     */
    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    /**
     * Gets the NDC code.
     *
     * @return the NDC code
     */
    public String getNdcCode() {
        return ndcCode;
    }

    /**
     * Sets the NDC code.
     *
     * @param ndcCode the NDC code to set
     */
    public void setNdcCode(String ndcCode) {
        this.ndcCode = ndcCode;
    }

    /**
     * Gets the manufacturer.
     *
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the manufacturer.
     *
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Gets the dosage form.
     *
     * @return the dosage form
     */
    public String getDosageForm() {
        return dosageForm;
    }

    /**
     * Sets the dosage form.
     *
     * @param dosageForm the dosage form to set
     */
    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    /**
     * Gets the route.
     *
     * @return the route
     */
    public String getRoute() {
        return route;
    }

    /**
     * Sets the route.
     *
     * @param route the route to set
     */
    public void setRoute(String route) {
        this.route = route;
    }

    /**
     * Checks if the drug is verified.
     *
     * @return true if verified, false otherwise
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * Sets the verified flag.
     *
     * @param verified the verified flag to set
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    /**
     * Gets the verification source.
     *
     * @return the source ("OpenFDA" or "Cache")
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the verification source.
     *
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }
}
