package com.medreceipt.security;

import com.medreceipt.model.User;
import com.medreceipt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService}
 * that loads user details from the database by email address.
 *
 * <p>This service bridges the application's {@link User} entity with
 * Spring Security's authentication mechanism. It converts the application
 * user into a Spring Security {@link UserDetails} object with the
 * appropriate granted authorities derived from the user's role.</p>
 *
 * @author medreceipt
 * @since 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address for authentication.
     *
     * <p>The email is used as the username in this system. The user's
     * {@code Role} enum value is prefixed with {@code ROLE_} to create
     * the granted authority, following Spring Security conventions.</p>
     *
     * @param email the email address of the user to load
     * @return a fully populated {@link UserDetails} instance
     * @throws UsernameNotFoundException if no user exists with the given email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException(
                            "User not found with email: " + email
                    );
                });

        GrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().name()
        );
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        logger.debug("User loaded successfully: {} with role: {}",
                email, user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true,                // enabled
                true,                // accountNonExpired
                true,                // credentialsNonExpired
                true,                // accountNonLocked
                authorities
        );
    }
}
