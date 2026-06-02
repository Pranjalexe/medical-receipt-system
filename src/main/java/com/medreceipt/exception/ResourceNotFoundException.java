package com.medreceipt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource cannot be found in the system.
 *
 * <p>This exception carries contextual information about which resource type
 * was being searched, which field was used for the lookup, and the value
 * that was not found. This information is used by the
 * {@link GlobalExceptionHandler} to construct meaningful error responses.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *   throw new ResourceNotFoundException("Receipt", "id", receiptId);
 * </pre>
 *
 * @author medreceipt
 * @since 1.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructs a new {@code ResourceNotFoundException} with a descriptive message.
     *
     * @param resourceName the name of the resource that was not found (e.g., "Receipt", "User")
     * @param fieldName    the field used for the lookup (e.g., "id", "email")
     * @param fieldValue   the value that was searched for
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Returns the name of the resource that was not found.
     *
     * @return the resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Returns the field name used for the lookup.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the field value that was searched for.
     *
     * @return the field value
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
