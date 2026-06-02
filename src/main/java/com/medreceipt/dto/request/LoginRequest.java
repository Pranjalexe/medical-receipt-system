package com.medreceipt.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user login requests.
 * Contains the credentials required for authentication.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class LoginRequest {

    /**
     * The user's email address used as their login identifier.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    /**
     * The user's password for authentication.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Default no-args constructor.
     */
    public LoginRequest() {
    }

    /**
     * All-args constructor.
     *
     * @param email    the user's email address
     * @param password the user's password
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
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
}
