# Medical Receipt Management System

A comprehensive Java Spring Boot 3 backend application designed to securely manage medical receipts for doctors and patients. 

## Features
- **Stateless REST API** with JWT authentication (`ROLE_DOCTOR`, `ROLE_PATIENT`).
- **Data Integrity**: Uses JPA `@Version` for optimistic locking, guaranteeing 100% data consistency for sensitive medical records.
- **External Integration**: OpenFDA drug database verification with multi-tier caching (Caffeine and Database) to reduce lookup latency.
- **PDF Generation**: Automated PDF creation using OpenPDF.

## Technologies Used
- Java 17
- Spring Boot 3.2.x
- Spring Security
- Spring Data JPA
- MySQL
- Caffeine Cache
- OpenPDF

## Getting Started

### Prerequisites
- JDK 17
- Maven
- MySQL database running on localhost:3306

### Configuration
Update the `src/main/resources/application-dev.yml` with your database credentials.

### Running the Application
```bash
./mvnw spring-boot:run
```

### Testing
```bash
./mvnw test
```
