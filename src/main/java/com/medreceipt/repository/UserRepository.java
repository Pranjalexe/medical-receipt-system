package com.medreceipt.repository;

import com.medreceipt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link User} entity.
 * Provides CRUD operations and custom query methods for user management.
 *
 * @author MedReceipt
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email address already exists.
     *
     * @param email the email address to check
     * @return true if a user with the email exists, false otherwise
     */
    Boolean existsByEmail(String email);
}
