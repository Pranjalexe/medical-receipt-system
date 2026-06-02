package com.medreceipt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Component responsible for JWT token creation, parsing, and validation.
 *
 * <p>Uses HMAC-SHA256 for signing tokens. The secret key and expiration
 * duration are externalized via application properties.</p>
 *
 * @author medreceipt
 * @since 1.0
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey signingKey;

    /**
     * Initializes the HMAC-SHA256 signing key from the configured secret.
     * Called automatically after dependency injection is complete.
     */
    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        logger.info("JWT signing key initialized successfully");
    }

    /**
     * Generates a JWT token for the authenticated user.
     *
     * <p>The token subject is set to the user's email address (username),
     * and includes the issued-at and expiration timestamps.</p>
     *
     * @param authentication the current authentication object
     * @return the signed JWT token string
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();

        logger.debug("Generated JWT token for user: {}", userDetails.getUsername());
        return token;
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param email the user's email address
     * @param id the user's ID
     * @param role the user's role
     * @return the signed JWT token string
     */
    public String generateToken(String email, Long id, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .subject(email)
                .claim("id", id)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();

        logger.debug("Generated JWT token for user: {}", email);
        return token;
    }

    /**
     * Extracts the user's email address from a JWT token.
     *
     * @param token the JWT token string
     * @return the email address stored as the token subject
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Validates a JWT token for structural integrity, signature, and expiration.
     *
     * @param token the JWT token string to validate
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Malformed JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty or null: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Returns the configured JWT expiration duration in milliseconds.
     *
     * @return the expiration duration in milliseconds
     */
    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
