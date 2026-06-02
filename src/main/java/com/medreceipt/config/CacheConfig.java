package com.medreceipt.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for Spring Cache and HTTP client infrastructure.
 * <p>
 * Configures a Caffeine-based cache manager for the drug verification cache
 * with a maximum of 500 entries and a 24-hour TTL. Also provides a
 * {@link RestTemplate} bean with connection and read timeouts of 5 seconds
 * for external API calls to OpenFDA.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    /** Maximum number of entries in the drug verification cache. */
    private static final long CACHE_MAX_SIZE = 500;

    /** Time-to-live for cache entries in hours. */
    private static final long CACHE_TTL_HOURS = 24;

    /** Connection timeout for RestTemplate in seconds. */
    private static final long CONNECTION_TIMEOUT_SECONDS = 5;

    /** Read timeout for RestTemplate in seconds. */
    private static final long READ_TIMEOUT_SECONDS = 5;

    /**
     * Creates and configures the Caffeine-based {@link CacheManager}.
     * <p>
     * The cache named {@code drugVerifications} is configured with:
     * <ul>
     *   <li>Maximum size: 500 entries</li>
     *   <li>Expiration: 24 hours after write</li>
     *   <li>Statistics recording enabled for monitoring</li>
     * </ul>
     * </p>
     *
     * @return the configured CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing Caffeine CacheManager with maxSize={}, TTL={}h", CACHE_MAX_SIZE, CACHE_TTL_HOURS);

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("drugVerifications");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(CACHE_MAX_SIZE)
                .expireAfterWrite(CACHE_TTL_HOURS, TimeUnit.HOURS)
                .recordStats());

        return cacheManager;
    }

    /**
     * Creates and configures the {@link RestTemplate} for external HTTP calls.
     * <p>
     * Configured with a 5-second connection timeout and a 5-second read timeout
     * to prevent hanging connections to the OpenFDA API.
     * </p>
     *
     * @param builder the Spring Boot RestTemplateBuilder for fluent configuration
     * @return the configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        log.info("Initializing RestTemplate with connectTimeout={}s, readTimeout={}s",
                CONNECTION_TIMEOUT_SECONDS, READ_TIMEOUT_SECONDS);

        return builder
                .setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECONDS))
                .setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS))
                .build();
    }
}
