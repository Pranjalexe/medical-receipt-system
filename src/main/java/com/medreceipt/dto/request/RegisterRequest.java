package com.medreceipt.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration requests.
 * Contains all fields needed for registering a new user (patient or doctor).
 * Doctor-specific fields (specialization, licenseNumber) are optional and only
 * required when registering as a doctor.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class RegisterRequest {

    /**
     * The user's email address, used as their unique login identifier.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    /**
     * The user's password. Must be at least 6 characters long.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    /**
     * The user's full name.
     */
    @NotBlank(message = "Full name is required")
    private String fullName;

    /**
     * The user's phone number (optional).
     */
    private String phone;

    /**
     * The role to assign to the user (e.g., "PATIENT", "DOCTOR", "ADMIN").
     */
    private String role;

    /**
     * The doctor's medical specialization (e.g., "Cardiology", "Dermatology").
     * Only applicable when registering as a doctor.
     */
    private String specialization;

    /**
     * The doctor's medical license number.
     * Only applicable when registering as a doctor.
     */
    private String licenseNumber;

    /**
     * The user's date of birth in ISO format (e.g., "1990-05-15").
     */
    private String dateOfBirth;

    /**
     * The user's residential or mailing address.
     */
    private String address;

    /**
     * The user's blood group (e.g., "A+", "O-", "AB+").
     */
    private String bloodGroup;

    /**
     * Default no-args constructor.
     */
    public RegisterRequest() {
    }

    /**
     * Gets the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name.
     *
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number.
     *
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the role.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the specialization.
     *
     * @return the specialization
     */
    public String getSpecialization() {
        return specialization;
    }

    /**
     * Sets the specialization.
     *
     * @param specialization the specialization to set
     */
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    /**
     * Gets the license number.
     *
     * @return the license number
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Sets the license number.
     *
     * @param licenseNumber the license number to set
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * Gets the date of birth.
     *
     * @return the date of birth as a string
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the date of birth.
     *
     * @param dateOfBirth the date of birth to set
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets the address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the blood group.
     *
     * @return the blood group
     */
    public String getBloodGroup() {
        return bloodGroup;
    }

    /**
     * Sets the blood group.
     *
     * @param bloodGroup the blood group to set
     */
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
}
