package com.medreceipt.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Data Transfer Object for creating a new prescription.
 * Contains the patient ID, list of prescribed items, and optional notes.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class PrescriptionRequest {

    /**
     * The ID of the patient for whom the prescription is being created.
     */
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    /**
     * The list of prescription items (drugs with dosage instructions).
     * Must contain at least one item.
     */
    @NotEmpty(message = "Prescription must contain at least one item")
    @Valid
    private List<PrescriptionItemRequest> items;

    /**
     * Optional notes or instructions from the doctor.
     */
    private String notes;

    /**
     * Default no-args constructor.
     */
    public PrescriptionRequest() {
    }

    /**
     * All-args constructor.
     *
     * @param patientId the patient ID
     * @param items     the list of prescription item requests
     * @param notes     optional notes
     */
    public PrescriptionRequest(Long patientId, List<PrescriptionItemRequest> items, String notes) {
        this.patientId = patientId;
        this.items = items;
        this.notes = notes;
    }

    /**
     * Gets the patient ID.
     *
     * @return the patient ID
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * Sets the patient ID.
     *
     * @param patientId the patient ID to set
     */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /**
     * Gets the list of prescription item requests.
     *
     * @return the list of prescription item requests
     */
    public List<PrescriptionItemRequest> getItems() {
        return items;
    }

    /**
     * Sets the list of prescription item requests.
     *
     * @param items the list of prescription item requests to set
     */
    public void setItems(List<PrescriptionItemRequest> items) {
        this.items = items;
    }

    /**
     * Gets the notes.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes.
     *
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
