package com.medreceipt.model;

import com.medreceipt.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user in the Digital Medical Receipt Management System.
 * <p>
 * This is the base user entity that holds authentication credentials and profile
 * information. A user is associated with a specific {@link Role} that determines
 * their access level. Doctors and patients have separate profile entities that
 * reference this user record.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Lifecycle Callbacks ──────────────────────────────────────────────

    /**
     * Sets the {@code createdAt} and {@code updatedAt} timestamps before
     * the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the {@code updatedAt} timestamp before the entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Constructors ─────────────────────────────────────────────────────

    /**
     * Default no-arg constructor required by JPA.
     */
    public User() {
    }

    /**
     * Constructs a new {@code User} with the essential fields.
     *
     * @param email    the user's email address (used for authentication)
     * @param password the user's hashed password
     * @param fullName the user's full display name
     * @param phone    the user's phone number (optional, may be {@code null})
     * @param role     the user's role within the system
     */
    public User(String email, String password, String fullName, String phone, Role role) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
    }

    // ── Getters & Setters ────────────────────────────────────────────────

    /**
     * Returns the unique identifier of this user.
     *
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this user.
     *
     * @param id the user ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the email address of this user.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of this user.
     *
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the hashed password of this user.
     *
     * @return the hashed password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the hashed password of this user.
     *
     * @param password the hashed password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the full name of this user.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of this user.
     *
     * @param fullName the full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Returns the phone number of this user.
     *
     * @return the phone number, or {@code null} if not set
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of this user.
     *
     * @param phone the phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the role of this user.
     *
     * @return the user role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of this user.
     *
     * @param role the user role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Returns the timestamp when this user was created.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when this user was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the timestamp when this user was last updated.
     *
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when this user was last updated.
     *
     * @param updatedAt the last update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ── Object Overrides ─────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
