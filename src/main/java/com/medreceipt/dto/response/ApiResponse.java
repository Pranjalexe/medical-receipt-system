package com.medreceipt.dto.response;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper used across all REST endpoints.
 * Provides a consistent structure for both successful and error responses.
 *
 * <p>Usage examples:</p>
 * <pre>
 *   // Success response with data
 *   ApiResponse.success("User created successfully", userData);
 *
 *   // Error response
 *   ApiResponse.error("Invalid credentials");
 * </pre>
 *
 * @author MedReceipt
 * @since 1.0
 */
public class ApiResponse {

    /**
     * Indicates whether the API call was successful.
     */
    private boolean success;

    /**
     * A human-readable message describing the result of the operation.
     */
    private String message;

    /**
     * The response payload. Can be any object (entity, list, map, etc.).
     * Will be null for error responses or operations with no return data.
     */
    private Object data;

    /**
     * The timestamp when this response was generated.
     */
    private LocalDateTime timestamp;

    /**
     * Default no-args constructor. Sets the timestamp to the current time.
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * All-args constructor.
     *
     * @param success   whether the operation was successful
     * @param message   a descriptive message
     * @param data      the response payload
     * @param timestamp the response timestamp
     */
    public ApiResponse(boolean success, String message, Object data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * Constructor without timestamp.
     *
     * @param success   whether the operation was successful
     * @param message   a descriptive message
     * @param data      the response payload
     */
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a successful API response with data.
     *
     * @param message a descriptive success message
     * @param data    the response payload
     * @return a new ApiResponse indicating success
     */
    public static ApiResponse success(String message, Object data) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Creates a successful API response without data.
     *
     * @param message a descriptive success message
     * @return a new ApiResponse indicating success with no data payload
     */
    public static ApiResponse success(String message) {
        return success(message, null);
    }

    /**
     * Creates an error API response.
     *
     * @param message a descriptive error message
     * @return a new ApiResponse indicating failure
     */
    public static ApiResponse error(String message) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * Checks if the operation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success flag.
     *
     * @param success the success flag to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the data payload.
     *
     * @return the data payload
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the data payload.
     *
     * @param data the data payload to set
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
