package com.medreceipt.exception;

import com.medreceipt.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler that intercepts exceptions thrown by controllers
 * and returns standardized JSON error responses.
 *
 * <p>Each handler method maps a specific exception type to an appropriate
 * HTTP status code and wraps the error details in an {@link ApiResponse}
 * object for consistent client-side error handling.</p>
 *
 * <h3>Handled Exceptions:</h3>
 * <ul>
 *   <li>{@link ResourceNotFoundException} → 404 Not Found</li>
 *   <li>{@link DrugVerificationException} → 503 Service Unavailable</li>
 *   <li>{@link MethodArgumentNotValidException} → 400 Bad Request (with field errors)</li>
 *   <li>{@link AccessDeniedException} → 403 Forbidden</li>
 *   <li>{@link Exception} → 500 Internal Server Error (catch-all)</li>
 * </ul>
 *
 * @author medreceipt
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link ResourceNotFoundException} when a requested resource
     * cannot be found in the database.
     *
     * @param ex the exception that was thrown
     * @return a 404 response with the error message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());

        ApiResponse response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link DrugVerificationException} when the external drug
     * verification service is unavailable or returns an error.
     *
     * @param ex the exception that was thrown
     * @return a 503 response with the error message
     */
    @ExceptionHandler(DrugVerificationException.class)
    public ResponseEntity<ApiResponse> handleDrugVerificationException(DrugVerificationException ex) {
        logger.error("Drug verification failed: {}", ex.getMessage());

        ApiResponse response = ApiResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles validation errors from {@code @Valid} annotated request bodies.
     *
     * <p>Extracts individual field errors and returns them as a map of
     * field name to error message, providing detailed feedback to the client
     * about which fields failed validation and why.</p>
     *
     * @param ex the validation exception containing field errors
     * @return a 400 response with a map of field-level validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {} field error(s)", ex.getBindingResult().getFieldErrorCount());

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Validation failed");

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            logger.debug("Validation error - field: '{}', rejected value: '{}', message: '{}'",
                    fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
        }
        body.put("errors", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link AccessDeniedException} when an authenticated user
     * attempts to access a resource they are not authorized for.
     *
     * @param ex the access denied exception
     * @return a 403 response with the error message
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());

        ApiResponse response = ApiResponse.error("Access denied: You do not have permission to access this resource");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Catch-all handler for any unhandled exceptions. Returns a generic
     * 500 Internal Server Error response without exposing internal details
     * to the client.
     *
     * @param ex the unhandled exception
     * @return a 500 response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ApiResponse response = ApiResponse.error("An unexpected error occurred. Please try again later.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
