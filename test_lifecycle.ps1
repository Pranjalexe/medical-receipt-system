$ErrorActionPreference = "Stop"

function Log {
    param([string]$message)
    Write-Host ">>> $message" -ForegroundColor Cyan
}

$baseUrl = "http://localhost:8080/api"

Log "Registering Doctor..."
$docRegReq = @{
    email = "testdoc@medreceipt.com"
    password = "password123"
    fullName = "Dr. Jane Smith"
    role = "ROLE_DOCTOR"
    specialization = "General Physician"
    licenseNumber = "LIC-12345"
} | ConvertTo-Json
try { Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $docRegReq -ContentType "application/json" | Out-Null; Log "Doctor registered." } catch { Log "Doctor already registered." }

Log "Registering Patient..."
$patRegReq = @{
    email = "testpat@medreceipt.com"
    password = "password123"
    fullName = "John Doe"
    role = "ROLE_PATIENT"
    dateOfBirth = "1990-01-01"
    address = "123 Main St"
    bloodGroup = "O+"
} | ConvertTo-Json
try { Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $patRegReq -ContentType "application/json" | Out-Null; Log "Patient registered." } catch { Log "Patient already registered." }

Log "Logging in as Doctor..."
$docLogin = @{ email="testdoc@medreceipt.com"; password="password123" } | ConvertTo-Json
$docResp = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $docLogin -ContentType "application/json"
$docToken = $docResp.data.token
$docAuth = @{ Authorization="Bearer $docToken" }
Log "Doctor logged in successfully."

Log "Fetching Patients for Doctor..."
$patientsResp = Invoke-RestMethod -Uri "$baseUrl/doctors/patients" -Method Get -Headers $docAuth
$patientId = ($patientsResp.data | Where-Object { $_.email -eq "testpat@medreceipt.com" }).id
Log "Found Patient ID: $patientId"

Log "Creating Prescription with 'paracetamol' (unverified) and 'advil' (verified)..."
$prescReq = @{ 
    patientId = $patientId
    notes = "Test prescription"
    items = @(
        @{ drugName="paracetamol"; dosage="500mg"; frequency="twice"; duration="5 days"; quantity=10 },
        @{ drugName="advil"; dosage="200mg"; frequency="once"; duration="2 days"; quantity=2 }
    )
} | ConvertTo-Json -Depth 10
$prescResp = Invoke-RestMethod -Uri "$baseUrl/doctors/prescriptions" -Method Post -Body $prescReq -ContentType "application/json" -Headers $docAuth
$prescriptionId = $prescResp.data.id
Log "Prescription created with ID: $prescriptionId"

Log "Generating Receipt for Prescription $prescriptionId..."
$receiptReq = @{
    prescriptionId = $prescriptionId
    paymentMethod = "CARD"
    discount = 5.00
    taxRate = 0.05
} | ConvertTo-Json
$receiptResp = Invoke-RestMethod -Uri "$baseUrl/doctors/receipts/generate" -Method Post -Body $receiptReq -ContentType "application/json" -Headers $docAuth
$receiptId = $receiptResp.data.id
$receiptNo = $receiptResp.data.receiptNumber
Log "Receipt generated with ID: $receiptId ($receiptNo)"

Log "Skipping Doctor Status Update so Admin can update it later..."

Log "Doctor Downloading PDF..."
$docPdfResp = Invoke-RestMethod -Uri "$baseUrl/receipts/$receiptId/pdf" -Method Get -Headers $docAuth -OutFile "test_doctor.pdf"
Log "Doctor successfully downloaded PDF. File exists: $(Test-Path test_doctor.pdf)"

Log "Logging in as Patient..."
$patLogin = @{ email="testpat@medreceipt.com"; password="password123" } | ConvertTo-Json
$patResp = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $patLogin -ContentType "application/json"
$patToken = $patResp.data.token
$patAuth = @{ Authorization="Bearer $patToken" }
Log "Patient logged in successfully."

Log "Patient Downloading PDF..."
$patPdfResp = Invoke-RestMethod -Uri "$baseUrl/receipts/$receiptId/pdf" -Method Get -Headers $patAuth -OutFile "test_patient.pdf"
Log "Patient successfully downloaded PDF. File exists: $(Test-Path test_patient.pdf)"

Log "Fetching Patient Prescriptions..."
$patPrescResp = Invoke-RestMethod -Uri "$baseUrl/patients/prescriptions" -Method Get -Headers $patAuth
Log "Patient has $($patPrescResp.data.Count) prescriptions."

Log "Fetching Patient Receipts..."
$patRecResp = Invoke-RestMethod -Uri "$baseUrl/patients/receipts" -Method Get -Headers $patAuth
Log "Patient has $($patRecResp.data.Count) receipts."

Log "Logging in as Admin..."
$adminLogin = @{ email="admin@medreceipt.com"; password="password" } | ConvertTo-Json
$adminResp = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $adminLogin -ContentType "application/json"
$adminToken = $adminResp.data.token
$adminAuth = @{ Authorization="Bearer $adminToken" }

Log "Admin Updating Receipt Status to PAID..."
$adminUpdateResp = Invoke-RestMethod -Uri "$baseUrl/receipts/$receiptId/status?status=PAID" -Method Put -Headers $adminAuth
Log "Admin successfully updated Receipt Status to: $($adminUpdateResp.data.status)"

Log "Fetching Admin Dashboard Stats..."
$adminStats = Invoke-RestMethod -Uri "$baseUrl/admin/stats" -Method Get -Headers $adminAuth
Log ("Admin Stats: Users=" + $adminStats.data.totalUsers + ", Receipts=" + $adminStats.data.totalReceipts)

Log "LIFECYCLE TEST COMPLETED SUCCESSFULLY!"
