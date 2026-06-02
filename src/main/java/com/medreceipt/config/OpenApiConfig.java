package com.medreceipt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * <p>
 * Defines the API metadata, contact information, and JWT Bearer authentication
 * security scheme. The generated documentation is accessible via Swagger UI at
 * {@code /swagger-ui.html} when the application is running.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Configuration
public class OpenApiConfig {

    /** Security scheme name used for JWT Bearer authentication. */
    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    /**
     * Creates and configures the {@link OpenAPI} bean with API metadata and security settings.
     * <p>
     * The configuration includes:
     * <ul>
     *   <li>API title, version, and description</li>
     *   <li>Contact information for the development team</li>
     *   <li>Apache 2.0 license reference</li>
     *   <li>JWT Bearer token security scheme</li>
     * </ul>
     * </p>
     *
     * @return the configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medical Receipt Management System API")
                        .version("1.0")
                        .description("A comprehensive Digital Medical Receipt Management System that provides "
                                + "RESTful APIs for managing prescriptions, generating medical receipts, "
                                + "verifying drugs against the OpenFDA database, and generating PDF receipts. "
                                + "The system supports role-based access for Doctors, Patients, and Admins.")
                        .contact(new Contact()
                                .name("MedReceipt Development Team")
                                .email("dev@medreceipt.com")
                                .url("https://medreceipt.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token obtained from the /api/auth/login endpoint. "
                                                + "Format: <token>")));
    }
}
