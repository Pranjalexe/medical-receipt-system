# Digital Medical Receipt System - Project Walkthrough & Setup Guide

Welcome to the **Digital Medical Receipt Management System**. This project is a robust, full-stack Spring Boot application with a modern, responsive HTML/JS/CSS frontend. It allows doctors to issue digital prescriptions and receipts, patients to view their medical records, and administrators to monitor system-wide statistics.

## ✨ Key Features

1. **Role-Based Access Control (RBAC)**: Secure JWT-based authentication for `ADMIN`, `DOCTOR`, and `PATIENT` roles.
2. **OpenFDA Integration**: Real-time pharmaceutical drug verification through the US FDA database.
3. **Resilient API Design**: Fallback caching mechanisms ensure prescriptions can still be issued even if OpenFDA goes down.
4. **Automated PDF Generation**: Professional, downloadable medical receipts generated dynamically on the server using OpenPDF.
5. **Admin Dashboard**: System-wide statistics and user management interface.
6. **Beautiful UI**: Modern "glassmorphism" aesthetic built completely with Vanilla CSS and JS (no heavy frameworks required).

---

## 🚀 Local Setup & Installation

Follow these instructions to run the project locally.

> [!IMPORTANT]
> **Prerequisites:**
> - Java 17+ (JDK)
> - Maven (or use the included `mvnw` wrapper)
> - PowerShell (for automated tests, optional)

### 1. Start the Backend Server

The backend runs on Spring Boot and uses an in-memory H2 database by default (so no database setup is required!).

1. Open your terminal and navigate to the project directory:
   ```bash
   cd medical-receipt-system
   ```
2. Start the server using Maven:
   ```bash
   ./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```
3. Wait until you see `Started MedReceiptApplication` in the console.

### 2. Access the Application

Once the server is running, you can access the frontend web application and API documentation:

- **Web UI:** [http://localhost:8080/](http://localhost:8080/)
- **Swagger API Docs:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:medical_receipt_db`, User: `SA`)

---

## 🛠️ Usage Workflow

### Default Administrator Account
The system automatically creates a master administrator account upon startup:
- **Email:** `admin@medreceipt.com`
- **Password:** `password`

### Recommended Testing Flow
1. Log in as the Admin to view system stats (which will initially be zeroed).
2. Register a new user and select the **Doctor** role.
3. Register another new user and select the **Patient** role.
4. Log out, then log in as the **Doctor**.
5. Navigate to **Issue Prescription**, select the patient, and add a drug (e.g. `advil` or `paracetamol`).
6. Navigate to **Prescriptions**, click **Generate Receipt**, apply tax/discount, and save.
7. Navigate to **Receipts** to see the new receipt. Click **Download PDF** to get the physical copy.
8. Log out and log in as the **Patient** to see their historical records and download their own PDFs.

> [!TIP]
> If a receipt is marked as `PENDING`, both Doctors and Admins have the ability to click **Mark Paid** to update the financial ledger.

---

## 🧪 Automated Testing

If you are on Windows, you can use the provided PowerShell lifecycle script to automatically populate the database with dummy data and test all critical endpoints.

```powershell
powershell -ExecutionPolicy Bypass -File .\test_lifecycle.ps1
```

This script will seamlessly:
- Register a test doctor and patient
- Log them in and fetch authentication tokens
- Create a prescription with both verified and unverified custom drugs
- Generate a receipt
- Download PDF artifacts for both parties
- Log in as the Admin and mark the receipt as PAID

---

## 📂 Project Structure

```
medical-receipt-system/
├── src/main/java/com/medreceipt/
│   ├── config/       # Security, Cache, and Swagger configurations
│   ├── controller/   # REST API Endpoints
│   ├── exception/    # Global error handling
│   ├── model/        # JPA Entities (User, Doctor, Receipt, etc.)
│   ├── repository/   # Spring Data JPA Interfaces
│   ├── security/     # JWT Token filtering and UserDetailsService
│   └── service/      # Business logic and PDF generation
├── src/main/resources/
│   ├── static/       # Frontend UI (index.html, app.js, index.css)
│   └── application.properties # Spring Boot configurations
├── pom.xml           # Maven dependencies
└── test_lifecycle.ps1# E2E automation script
```
