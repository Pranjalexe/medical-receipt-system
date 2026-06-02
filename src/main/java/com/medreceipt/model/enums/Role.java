package com.medreceipt.model.enums;

/**
 * Enumeration representing user roles in the Digital Medical Receipt Management System.
 * <p>
 * Each role defines a distinct set of permissions and access levels within the application.
 * The {@code ROLE_} prefix follows Spring Security naming conventions for granted authorities.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
public enum Role {

    /**
     * Doctor role — can create prescriptions and generate receipts.
     */
    ROLE_DOCTOR,

    /**
     * Patient role — can view their own prescriptions and receipts.
     */
    ROLE_PATIENT,

    /**
     * Admin role — has full access to all system resources.
     */
    ROLE_ADMIN
}
