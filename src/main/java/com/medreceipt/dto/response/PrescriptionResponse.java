package com.medreceipt.dto.response;

import java.util.List;

/**
 * Data Transfer Object for prescription responses.
 * Contains the full details of a prescription including the prescribing doctor,
 * the patient, prescribed items, and timestamps.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class PrescriptionResponse {

    /**
     * The unique database ID of the prescription.
     */
    private Long id;

    /**
     * The full name of the prescribing doctor.
     */
    private String doctorName;

    /**
     * The full name of the patient.
     */
    private String patientName;

    /**
     * The list of prescription items (drugs with dosage instructions).
     */
    private List<PrescriptionItemResponse> items;

    /**
     * Optional notes or special instructions from the doctor.
     */
    private String notes;

    /**
     * The date the prescription was issued, formatted as a string (e.g., "2026-06-02").
     */
    private String issueDate;

    /**
     * The timestamp when the prescription record was created, formatted as a string.
     */
    private String createdAt;

    /**
     * Default no-args constructor.
     */
    public PrescriptionResponse() {
    }

    /**
     * Gets the prescription ID.
     *
     * @return the prescription ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the prescription ID.
     *
     * @param id the prescription ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the doctor name.
     *
     * @return the doctor name
     */
    public String getDoctorName() {
        return doctorName;
    }

    /**
     * Sets the doctor name.
     *
     * @param doctorName the doctor name to set
     */
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    /**
     * Gets the patient name.
     *
     * @return the patient name
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * Sets the patient name.
     *
     * @param patientName the patient name to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * Gets the list of prescription items.
     *
     * @return the list of prescription item responses
     */
    public List<PrescriptionItemResponse> getItems() {
        return items;
    }

    /**
     * Sets the list of prescription items.
     *
     * @param items the list of prescription item responses to set
     */
    public void setItems(List<PrescriptionItemResponse> items) {
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

    /**
     * Gets the issue date.
     *
     * @return the issue date as a string
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * Sets the issue date.
     *
     * @param issueDate the issue date to set
     */
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * Gets the created-at timestamp.
     *
     * @return the created-at timestamp as a string
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the created-at timestamp.
     *
     * @param createdAt the created-at timestamp to set
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
