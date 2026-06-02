package com.medreceipt.controller;

import com.medreceipt.dto.response.AdminStatsResponse;
import com.medreceipt.dto.response.AdminUserResponse;
import com.medreceipt.dto.response.ApiResponse;
import com.medreceipt.dto.response.ReceiptResponse;
import com.medreceipt.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getSystemStats() {
        AdminStatsResponse stats = adminService.getSystemStats();
        return ResponseEntity.ok(ApiResponse.success("System stats retrieved successfully", stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<AdminUserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @GetMapping("/receipts")
    public ResponseEntity<ApiResponse> getAllReceipts() {
        List<ReceiptResponse> receipts = adminService.getAllReceipts();
        return ResponseEntity.ok(ApiResponse.success("Receipts retrieved successfully", receipts));
    }
}
