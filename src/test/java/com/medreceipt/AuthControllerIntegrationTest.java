package com.medreceipt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medreceipt.dto.request.LoginRequest;
import com.medreceipt.dto.request.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("dev")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testDoctorRegistrationAndLogin() throws Exception {
        // Register
        RegisterRequest regReq = new RegisterRequest();
        regReq.setFullName("Test Doctor");
        regReq.setEmail("testdoc@medreceipt.com");
        regReq.setPassword("password123");
        regReq.setRole("ROLE_DOCTOR");
        regReq.setSpecialization("Cardiology");
        regReq.setLicenseNumber("DOC12345");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("ROLE_DOCTOR"));

        // Login
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("testdoc@medreceipt.com");
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("ROLE_DOCTOR"));
    }

    @Test
    public void testPatientRegistrationAndLogin() throws Exception {
        // Register
        RegisterRequest regReq = new RegisterRequest();
        regReq.setFullName("Test Patient");
        regReq.setEmail("testpat@medreceipt.com");
        regReq.setPassword("password123");
        regReq.setRole("ROLE_PATIENT");
        regReq.setDateOfBirth("1990-01-01");
        regReq.setAddress("123 Main St");
        regReq.setBloodGroup("O+");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("ROLE_PATIENT"));

        // Login
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("testpat@medreceipt.com");
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("ROLE_PATIENT"));
    }
}
