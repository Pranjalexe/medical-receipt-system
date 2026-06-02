package com.medreceipt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when drug verification against an external service fails.
 *
 * <p>This typically occurs when the external drug verification API
 * (e.g., OpenFDA) is unavailable, returns an error, or when the
 * verification process encounters an unexpected condition.</p>
 *
 * <p>Mapped to HTTP 503 (Service Unavailable) by the
 * {@link GlobalExceptionHandler} since it indicates a dependency failure.</p>
 *
 * @author medreceipt
 * @since 1.0
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class DrugVerificationException extends RuntimeException {

    /**
     * Constructs a new {@code DrugVerificationException} with the specified detail message.
     *
     * @param message the detail message explaining the verification failure
     */
    public DrugVerificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code DrugVerificationException} with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the verification failure
     * @param cause   the underlying cause of the failure
     */
    public DrugVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
