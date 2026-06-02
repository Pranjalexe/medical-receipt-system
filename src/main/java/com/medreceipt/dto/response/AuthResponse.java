package com.medreceipt.dto.response;

/**
 * Data Transfer Object for authentication responses.
 * Returned after a successful login or registration, containing the JWT token
 * and basic user information.
 *
 * @author MedReceipt
 * @since 1.0
 */
public class AuthResponse {

    /**
     * The JWT authentication token.
     */
    private String token;

    /**
     * The token type, defaults to "Bearer" for JWT-based authentication.
     */
    private String tokenType = "Bearer";

    /**
     * The authenticated user's email address.
     */
    private String email;

    /**
     * The authenticated user's full name.
     */
    private String fullName;

    /**
     * The authenticated user's role (e.g., "PATIENT", "DOCTOR", "ADMIN").
     */
    private String role;

    /**
     * Default no-args constructor.
     */
    public AuthResponse() {
    }

    /**
     * Constructor with token only. Sets tokenType to "Bearer" by default.
     *
     * @param token the JWT token
     */
    public AuthResponse(String token) {
        this.token = token;
    }

    /**
     * All-args constructor.
     *
     * @param token     the JWT token
     * @param tokenType the token type
     * @param email     the user's email
     * @param fullName  the user's full name
     * @param role      the user's role
     */
    public AuthResponse(String token, String tokenType, String email, String fullName, String role) {
        this.token = token;
        this.tokenType = tokenType;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    /**
     * Gets the JWT token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the JWT token.
     *
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the token type.
     *
     * @return the token type
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Sets the token type.
     *
     * @param tokenType the token type to set
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
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
}
