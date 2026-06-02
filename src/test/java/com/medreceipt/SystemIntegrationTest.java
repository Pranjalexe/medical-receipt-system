package com.medreceipt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medreceipt.dto.request.LoginRequest;
import com.medreceipt.dto.request.PrescriptionItemRequest;
import com.medreceipt.dto.request.PrescriptionRequest;
import com.medreceipt.dto.request.RegisterRequest;
import com.medreceipt.dto.request.ReceiptRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setupMock() {
        // Mock OpenFDA response
        String mockJsonResponse = """
                {
                  "results": [
                    {
                      "brand_name": "Advil",
                      "generic_name": "Ibuprofen",
                      "product_ndc": "1234-5678",
                      "labeler_name": "Pfizer",
                      "dosage_form": "TABLET",
                      "route": ["ORAL"]
                    }
                  ]
                }
                """;
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockJsonResponse);
    }

    @Test
    void testFullSystemLifecycle() throws Exception {
        // 1. Register Doctor
        RegisterRequest doctorReg = new RegisterRequest();
        doctorReg.setEmail("doctor@test.com");
        doctorReg.setPassword("password123");
        doctorReg.setFullName("Dr. Smith");
        doctorReg.setRole("ROLE_DOCTOR");
        doctorReg.setSpecialization("General");
        doctorReg.setLicenseNumber("LIC12345");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorReg)))
                .andExpect(status().isCreated());

        // 2. Register Patient
        RegisterRequest patientReg = new RegisterRequest();
        patientReg.setEmail("patient@test.com");
        patientReg.setPassword("password123");
        patientReg.setFullName("John Doe");
        patientReg.setRole("ROLE_PATIENT");
        patientReg.setDateOfBirth("1990-01-01");
        patientReg.setAddress("123 Test St");
        patientReg.setBloodGroup("O+");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientReg)))
                .andExpect(status().isCreated());

        // 3. Login Doctor
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("doctor@test.com");
        loginReq.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponseStr = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(loginResponseStr).get("data").get("token").asText();

        // 4. Create Prescription
        PrescriptionRequest presReq = new PrescriptionRequest();
        presReq.setPatientId(1L); // Patient is probably ID 1
        presReq.setNotes("Take with food");
        
        PrescriptionItemRequest itemReq = new PrescriptionItemRequest();
        itemReq.setDrugName("Advil");
        itemReq.setDosage("200mg");
        itemReq.setFrequency("Twice a day");
        itemReq.setDuration("5 days");
        itemReq.setQuantity(10);

        presReq.setItems(Collections.singletonList(itemReq));

        MvcResult presResult = mockMvc.perform(post("/api/doctors/prescriptions")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(presReq)))
                .andExpect(status().isCreated()) // If this fails, we found a bug!
                .andReturn();

        String presResponseStr = presResult.getResponse().getContentAsString();
        Long presId = objectMapper.readTree(presResponseStr).get("data").get("id").asLong();

        // 5. Generate Receipt
        ReceiptRequest receiptReq = new ReceiptRequest();
        receiptReq.setPrescriptionId(presId);
        receiptReq.setDiscount(BigDecimal.ZERO);
        receiptReq.setTaxRate(BigDecimal.valueOf(5.0));
        receiptReq.setPaymentMethod("CARD");

        mockMvc.perform(post("/api/doctors/receipts/generate")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receiptReq)))
                .andExpect(status().isCreated()); // If this fails, we found a bug!
    }
}
