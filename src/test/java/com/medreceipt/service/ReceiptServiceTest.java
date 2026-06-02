package com.medreceipt.service;

import com.medreceipt.dto.response.ReceiptResponse;
import com.medreceipt.exception.ResourceNotFoundException;
import com.medreceipt.model.Receipt;
import com.medreceipt.model.enums.ReceiptStatus;
import com.medreceipt.repository.ReceiptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReceiptServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getReceiptByNumber_Success() {
        Receipt receipt = new Receipt();
        receipt.setId(1L);
        receipt.setReceiptNumber("MR-20240101-00001");
        receipt.setStatus(ReceiptStatus.PENDING);

        when(receiptRepository.findByReceiptNumber("MR-20240101-00001")).thenReturn(Optional.of(receipt));

        ReceiptResponse response = receiptService.getReceiptByNumber("MR-20240101-00001");

        assertNotNull(response);
        assertEquals("MR-20240101-00001", response.getReceiptNumber());
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void getReceiptByNumber_NotFound() {
        when(receiptRepository.findByReceiptNumber("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> receiptService.getReceiptByNumber("UNKNOWN"));
    }
}
