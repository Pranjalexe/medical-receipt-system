package com.medreceipt.dto.response;

import java.math.BigDecimal;

public class AdminStatsResponse {
    private long totalUsers;
    private long totalDoctors;
    private long totalPatients;
    private long totalPrescriptions;
    private long totalReceipts;
    private BigDecimal totalRevenue;

    public AdminStatsResponse() {}

    public AdminStatsResponse(long totalUsers, long totalDoctors, long totalPatients, long totalPrescriptions, long totalReceipts, BigDecimal totalRevenue) {
        this.totalUsers = totalUsers;
        this.totalDoctors = totalDoctors;
        this.totalPatients = totalPatients;
        this.totalPrescriptions = totalPrescriptions;
        this.totalReceipts = totalReceipts;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getTotalPrescriptions() {
        return totalPrescriptions;
    }

    public void setTotalPrescriptions(long totalPrescriptions) {
        this.totalPrescriptions = totalPrescriptions;
    }

    public long getTotalReceipts() {
        return totalReceipts;
    }

    public void setTotalReceipts(long totalReceipts) {
        this.totalReceipts = totalReceipts;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
