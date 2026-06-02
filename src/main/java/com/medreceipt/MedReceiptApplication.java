package com.medreceipt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Medical Receipt Management System.
 * 
 * This application provides a scalable backend for managing digital medical receipts,
 * with automated portals for doctors and patients, and integration with the OpenFDA
 * drug database for prescription verification.
 * 
 * @author MedReceipt Team
 * @version 1.0.0
 */
@SpringBootApplication
public class MedReceiptApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedReceiptApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner initAdmin(
            com.medreceipt.repository.UserRepository userRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByEmail("admin@medreceipt.com").ifPresent(admin -> {
                admin.setPassword(passwordEncoder.encode("password"));
                userRepository.save(admin);
                System.out.println("Admin password updated successfully to 'password'");
            });
        };
    }
}
