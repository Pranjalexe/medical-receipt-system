package com.medreceipt.service;

import com.medreceipt.dto.response.AdminStatsResponse;
import com.medreceipt.dto.response.AdminUserResponse;
import com.medreceipt.dto.response.ReceiptResponse;
import com.medreceipt.dto.response.ReceiptItemResponse;
import com.medreceipt.model.User;
import com.medreceipt.model.Receipt;
import com.medreceipt.repository.DoctorRepository;
import com.medreceipt.repository.PatientRepository;
import com.medreceipt.repository.PrescriptionRepository;
import com.medreceipt.repository.ReceiptRepository;
import com.medreceipt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ReceiptRepository receiptRepository;

    public AdminService(UserRepository userRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository,
                        PrescriptionRepository prescriptionRepository,
                        ReceiptRepository receiptRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.receiptRepository = receiptRepository;
    }

    public AdminStatsResponse getSystemStats() {
        long totalUsers = userRepository.count();
        long totalDoctors = doctorRepository.count();
        long totalPatients = patientRepository.count();
        long totalPrescriptions = prescriptionRepository.count();
        long totalReceipts = receiptRepository.count();
        BigDecimal totalRevenue = receiptRepository.sumNetAmounts();

        return new AdminStatsResponse(totalUsers, totalDoctors, totalPatients, totalPrescriptions, totalReceipts, totalRevenue);
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToAdminUserResponse).collect(Collectors.toList());
    }

    public List<ReceiptResponse> getAllReceipts() {
        return receiptRepository.findAll().stream().map(this::mapToReceiptResponse).collect(Collectors.toList());
    }

    private AdminUserResponse mapToAdminUserResponse(User user) {
        AdminUserResponse response = new AdminUserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setPhone(user.getPhone());
        response.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        return response;
    }

    private ReceiptResponse mapToReceiptResponse(Receipt receipt) {
        ReceiptResponse dto = new ReceiptResponse();
        dto.setId(receipt.getId());
        dto.setReceiptNumber(receipt.getReceiptNumber());
        dto.setPatientName(receipt.getPatient().getUser().getFullName());
        dto.setDoctorName(receipt.getDoctor().getUser().getFullName());
        dto.setTotalAmount(receipt.getTotalAmount());
        dto.setDiscount(receipt.getDiscount());
        dto.setTaxAmount(receipt.getTaxAmount());
        dto.setNetAmount(receipt.getNetAmount());
        dto.setStatus(receipt.getStatus().name());
        dto.setPaymentMethod(receipt.getPaymentMethod().name());
        dto.setGeneratedAt(receipt.getGeneratedAt().toString());
        
        if (receipt.getItems() != null) {
            List<ReceiptItemResponse> items = receipt.getItems().stream().map(item -> {
                ReceiptItemResponse itemDto = new ReceiptItemResponse();
                itemDto.setDrugName(item.getDrug().getBrandName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPriceAtSale(item.getPriceAtSale());
                itemDto.setSubtotal(item.getSubtotal());
                return itemDto;
            }).collect(Collectors.toList());
            dto.setItems(items);
        }
        return dto;
    }
}
