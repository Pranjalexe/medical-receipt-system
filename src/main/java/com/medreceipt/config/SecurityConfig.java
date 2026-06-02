package com.medreceipt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medreceipt.dto.response.ApiResponse;
import com.medreceipt.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central Spring Security configuration for the Medical Receipt Management System.
 *
 * <p>Configures the application as a stateless REST API with JWT-based
 * authentication. CSRF protection is disabled since the API is stateless
 * and relies on Bearer tokens. CORS is enabled with default configuration
 * for cross-origin requests.</p>
 *
 * <h3>Public Endpoints:</h3>
 * <ul>
 *   <li>{@code /api/auth/**} – Authentication (login, register)</li>
 *   <li>{@code /api/drugs/search} – Public drug search</li>
 *   <li>{@code /swagger-ui/**} – Swagger UI</li>
 *   <li>{@code /v3/api-docs/**} – OpenAPI documentation</li>
 * </ul>
 *
 * <p>All other endpoints require a valid JWT token.</p>
 *
 * @author medreceipt
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the HTTP security filter chain.
     *
     * <p>Sets up stateless session management, disables CSRF, configures
     * public/protected URL patterns, adds the JWT filter, and registers
     * a custom authentication entry point that returns a JSON 401 response.</p>
     *
     * @param http the {@link HttpSecurity} builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/drugs/search").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            logger.warn("Unauthorized access attempt to: {} - {}",
                                    request.getRequestURI(), authException.getMessage());

                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            ApiResponse apiResponse = new ApiResponse(
                                    false,
                                    "Unauthorized: Authentication is required to access this resource",
                                    null
                            );

                            ObjectMapper mapper = new ObjectMapper();
                            response.getWriter().write(mapper.writeValueAsString(apiResponse));
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("Security filter chain configured successfully");
        return http.build();
    }

    /**
     * Creates a BCrypt password encoder bean for hashing and verifying passwords.
     *
     * @return a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the {@link AuthenticationManager} as a Spring bean, allowing
     * it to be injected into services that need to perform programmatic authentication.
     *
     * @param authenticationConfiguration the Spring-managed authentication configuration
     * @return the {@link AuthenticationManager}
     * @throws Exception if the manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
