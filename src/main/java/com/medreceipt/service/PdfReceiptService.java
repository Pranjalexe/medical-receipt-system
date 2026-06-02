package com.medreceipt.service;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import com.medreceipt.model.Doctor;
import com.medreceipt.model.Patient;
import com.medreceipt.model.Receipt;
import com.medreceipt.model.ReceiptItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Service class for generating professional PDF receipt documents using OpenPDF.
 * <p>
 * Produces formatted PDF files containing:
 * <ul>
 *   <li>Clinic header with organization name and branding</li>
 *   <li>Receipt number and date information</li>
 *   <li>Patient and doctor details</li>
 *   <li>Itemized table of medicines with dosage, quantity, and pricing</li>
 *   <li>Financial summary with subtotal, discount, tax, and net total</li>
 *   <li>Footer with payment method and receipt status</li>
 * </ul>
 * PDFs are saved to a configurable filesystem path.
 * </p>
 *
 * @author MedReceipt
 * @since 1.0
 */
@Service
public class PdfReceiptService {

    private static final Logger log = LoggerFactory.getLogger(PdfReceiptService.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // Font definitions using Helvetica family
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.DARK_GRAY);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
    private static final Font LABEL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.DARK_GRAY);
    private static final Font VALUE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Font TABLE_CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
    private static final Font TOTAL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);

    /** Configurable storage path for generated PDF receipts. */
    @Value("${app.receipt.storage-path:./receipts}")
    private String storagePath;

    /**
     * Generates a PDF receipt document for the given receipt entity.
     * <p>
     * The PDF is saved to the configured storage directory with the filename
     * format: {@code receipt_{receiptNumber}_{timestamp}.pdf}.
     * </p>
     *
     * @param receipt the receipt entity containing all data to render
     * @return the absolute filesystem path to the generated PDF file
     * @throws RuntimeException if PDF generation or file writing fails
     */
    public String generateReceiptPdf(Receipt receipt) {
        log.info("Generating PDF for receipt: {}", receipt.getReceiptNumber());

        // Ensure storage directory exists
        File storageDir = new File(storagePath);
        if (!storageDir.exists()) {
            boolean created = storageDir.mkdirs();
            if (created) {
                log.info("Created receipt storage directory: {}", storagePath);
            } else {
                log.warn("Failed to create storage directory: {}", storagePath);
            }
        }

        // Build file path
        String timestamp = receipt.getGeneratedAt() != null
                ? receipt.getGeneratedAt().format(FILE_DATE_FORMATTER)
                : "unknown";
        String fileName = "receipt_" + receipt.getReceiptNumber() + "_" + timestamp + ".pdf";
        File pdfFile = new File(storageDir, fileName);
        String absolutePath = pdfFile.getAbsolutePath();

        Document document = new Document(PageSize.A4, 40, 40, 50, 50);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // === CLINIC HEADER ===
            addClinicHeader(document);

            // === RECEIPT INFO ===
            addReceiptInfo(document, receipt);

            // === PATIENT & DOCTOR INFO ===
            addPartyInfo(document, receipt);

            // === ITEMS TABLE ===
            addItemsTable(document, receipt);

            // === FINANCIAL SUMMARY ===
            addFinancialSummary(document, receipt);

            // === FOOTER ===
            addFooter(document, receipt);

            document.close();
            log.info("PDF generated successfully at: {}", absolutePath);

        } catch (DocumentException | IOException e) {
            log.error("Failed to generate PDF for receipt {}: {}", receipt.getReceiptNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF receipt: " + e.getMessage(), e);
        }

        return absolutePath;
    }

    /**
     * Adds the clinic header section to the PDF document.
     *
     * @param document the PDF document
     * @throws DocumentException if the content cannot be added
     */
    private void addClinicHeader(Document document) throws DocumentException {
        Paragraph clinicName = new Paragraph("MedReceipt Healthcare", TITLE_FONT);
        clinicName.setAlignment(Element.ALIGN_CENTER);
        document.add(clinicName);

        Paragraph clinicSubtitle = new Paragraph(
                "Digital Medical Receipt Management System", SUBTITLE_FONT);
        clinicSubtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(clinicSubtitle);

        Paragraph clinicContact = new Paragraph(
                "Email: info@medreceipt.com | Phone: +91-1234567890", SUBTITLE_FONT);
        clinicContact.setAlignment(Element.ALIGN_CENTER);
        clinicContact.setSpacingAfter(5);
        document.add(clinicContact);

        // Horizontal separator line
        Paragraph separator = new Paragraph(
                "─────────────────────────────────────────────────────────────────────────────",
                SUBTITLE_FONT);
        separator.setAlignment(Element.ALIGN_CENTER);
        separator.setSpacingAfter(10);
        document.add(separator);
    }

    /**
     * Adds receipt metadata (number, date, status) to the PDF document.
     *
     * @param document the PDF document
     * @param receipt  the receipt entity
     * @throws DocumentException if the content cannot be added
     */
    private void addReceiptInfo(Document document, Receipt receipt) throws DocumentException {
        Paragraph receiptHeader = new Paragraph("MEDICAL RECEIPT", HEADER_FONT);
        receiptHeader.setAlignment(Element.ALIGN_CENTER);
        receiptHeader.setSpacingAfter(10);
        document.add(receiptHeader);

        Paragraph receiptNumber = new Paragraph();
        receiptNumber.add(new Phrase("Receipt No: ", LABEL_FONT));
        receiptNumber.add(new Phrase(receipt.getReceiptNumber(), VALUE_FONT));
        document.add(receiptNumber);

        Paragraph date = new Paragraph();
        date.add(new Phrase("Date: ", LABEL_FONT));
        String dateStr = receipt.getGeneratedAt() != null
                ? receipt.getGeneratedAt().format(DATE_FORMATTER)
                : "N/A";
        date.add(new Phrase(dateStr, VALUE_FONT));
        date.setSpacingAfter(15);
        document.add(date);
    }

    /**
     * Adds patient and doctor information sections to the PDF document.
     *
     * @param document the PDF document
     * @param receipt  the receipt entity
     * @throws DocumentException if the content cannot be added
     */
    private void addPartyInfo(Document document, Receipt receipt) throws DocumentException {
        // Patient info
        Paragraph patientHeader = new Paragraph("Patient Information", HEADER_FONT);
        patientHeader.setSpacingAfter(3);
        document.add(patientHeader);

        Patient patient = receipt.getPatient();
        if (patient != null && patient.getUser() != null) {
            addLabelValueLine(document, "Name: ", patient.getUser().getFullName());
            addLabelValueLine(document, "Phone: ", patient.getUser().getPhone());
            if (patient.getAddress() != null) {
                addLabelValueLine(document, "Address: ", patient.getAddress());
            }
        }

        Paragraph spacer1 = new Paragraph(" ");
        spacer1.setSpacingAfter(5);
        document.add(spacer1);

        // Doctor info
        Paragraph doctorHeader = new Paragraph("Doctor Information", HEADER_FONT);
        doctorHeader.setSpacingAfter(3);
        document.add(doctorHeader);

        Doctor doctor = receipt.getDoctor();
        if (doctor != null && doctor.getUser() != null) {
            addLabelValueLine(document, "Name: ", "Dr. " + doctor.getUser().getFullName());
            addLabelValueLine(document, "Specialization: ", doctor.getSpecialization());
            addLabelValueLine(document, "License No: ", doctor.getLicenseNumber());
        }

        Paragraph spacer2 = new Paragraph(" ");
        spacer2.setSpacingAfter(10);
        document.add(spacer2);
    }

    /**
     * Adds the itemized medicine table to the PDF document.
     *
     * @param document the PDF document
     * @param receipt  the receipt entity
     * @throws DocumentException if the table cannot be added
     */
    private void addItemsTable(Document document, Receipt receipt) throws DocumentException {
        Paragraph itemsHeader = new Paragraph("Prescribed Medicines", HEADER_FONT);
        itemsHeader.setSpacingAfter(5);
        document.add(itemsHeader);

        // Create table with 5 columns: Medicine, Dosage, Qty, Unit Price, Subtotal
        Table table = new Table(5);
        table.setWidth(100);
        table.setPadding(5);
        table.setSpacing(0);

        float[] columnWidths = {35f, 20f, 10f, 15f, 20f};
        table.setWidths(columnWidths);

        // Table headers with dark background
        Color headerBg = new Color(52, 73, 94);
        addTableHeaderCell(table, "Medicine", headerBg);
        addTableHeaderCell(table, "Dosage", headerBg);
        addTableHeaderCell(table, "Qty", headerBg);
        addTableHeaderCell(table, "Unit Price", headerBg);
        addTableHeaderCell(table, "Subtotal", headerBg);

        // Table data rows
        if (receipt.getItems() != null) {
            boolean alternate = false;
            for (ReceiptItem item : receipt.getItems()) {
                Color rowBg = alternate ? new Color(245, 245, 245) : Color.WHITE;

                String drugName = item.getDrug() != null ? item.getDrug().getBrandName() : "N/A";
                String dosage = item.getDrug() != null ? item.getDrug().getDosageForm() : "N/A";

                addTableDataCell(table, drugName, rowBg);
                addTableDataCell(table, dosage, rowBg);
                addTableDataCell(table, String.valueOf(item.getQuantity()), rowBg);
                addTableDataCell(table, formatCurrency(item.getPriceAtSale()), rowBg);
                addTableDataCell(table, formatCurrency(item.getSubtotal()), rowBg);

                alternate = !alternate;
            }
        }

        document.add(table);

        Paragraph spacer = new Paragraph(" ");
        spacer.setSpacingAfter(10);
        document.add(spacer);
    }

    /**
     * Adds the financial summary section (subtotal, discount, tax, net total) to the PDF.
     *
     * @param document the PDF document
     * @param receipt  the receipt entity
     * @throws DocumentException if the content cannot be added
     */
    private void addFinancialSummary(Document document, Receipt receipt) throws DocumentException {
        Paragraph summaryHeader = new Paragraph("Payment Summary", HEADER_FONT);
        summaryHeader.setSpacingAfter(5);
        document.add(summaryHeader);

        // Subtotal
        addSummaryLine(document, "Subtotal:", formatCurrency(receipt.getTotalAmount()), false);

        // Discount
        String discountText = formatCurrency(receipt.getDiscount());
        addSummaryLine(document, "Discount:", "- " + discountText, false);

        // Tax
        addSummaryLine(document, "Tax (GST):", "+ " + formatCurrency(receipt.getTaxAmount()), false);

        // Separator
        Paragraph separatorLine = new Paragraph(
                "─────────────────────────────────────────", SUBTITLE_FONT);
        separatorLine.setAlignment(Element.ALIGN_RIGHT);
        document.add(separatorLine);

        // Net Total (bold)
        addSummaryLine(document, "NET TOTAL:", formatCurrency(receipt.getNetAmount()), true);

        Paragraph spacer = new Paragraph(" ");
        spacer.setSpacingAfter(15);
        document.add(spacer);
    }

    /**
     * Adds the footer section with payment method and status to the PDF.
     *
     * @param document the PDF document
     * @param receipt  the receipt entity
     * @throws DocumentException if the content cannot be added
     */
    private void addFooter(Document document, Receipt receipt) throws DocumentException {
        // Separator
        Paragraph separator = new Paragraph(
                "─────────────────────────────────────────────────────────────────────────────",
                SUBTITLE_FONT);
        separator.setAlignment(Element.ALIGN_CENTER);
        separator.setSpacingAfter(5);
        document.add(separator);

        // Payment method and status
        Paragraph paymentInfo = new Paragraph();
        paymentInfo.add(new Phrase("Payment Method: ", LABEL_FONT));
        paymentInfo.add(new Phrase(
                receipt.getPaymentMethod() != null ? receipt.getPaymentMethod().name() : "N/A", VALUE_FONT));
        document.add(paymentInfo);

        Paragraph statusInfo = new Paragraph();
        statusInfo.add(new Phrase("Status: ", LABEL_FONT));
        statusInfo.add(new Phrase(
                receipt.getStatus() != null ? receipt.getStatus().name() : "PENDING", VALUE_FONT));
        statusInfo.setSpacingAfter(10);
        document.add(statusInfo);

        // Footer note
        Paragraph footerNote = new Paragraph(
                "This is a computer-generated receipt. No signature is required.", FOOTER_FONT);
        footerNote.setAlignment(Element.ALIGN_CENTER);
        document.add(footerNote);

        Paragraph generatedBy = new Paragraph(
                "Generated by MedReceipt Healthcare Management System © 2026", FOOTER_FONT);
        generatedBy.setAlignment(Element.ALIGN_CENTER);
        document.add(generatedBy);
    }

    /**
     * Adds a label-value line to the PDF document.
     *
     * @param document the PDF document
     * @param label    the label text (bold)
     * @param value    the value text (regular)
     * @throws DocumentException if the content cannot be added
     */
    private void addLabelValueLine(Document document, String label, String value)
            throws DocumentException {
        Paragraph line = new Paragraph();
        line.add(new Phrase(label, LABEL_FONT));
        line.add(new Phrase(value != null ? value : "N/A", VALUE_FONT));
        document.add(line);
    }

    /**
     * Adds a summary line (right-aligned label and value) to the financial summary section.
     *
     * @param document the PDF document
     * @param label    the summary label
     * @param value    the formatted monetary value
     * @param isBold   whether to render in bold (for totals)
     * @throws DocumentException if the content cannot be added
     */
    private void addSummaryLine(Document document, String label, String value, boolean isBold)
            throws DocumentException {
        Font font = isBold ? TOTAL_FONT : VALUE_FONT;
        Font labelFont = isBold ? TOTAL_FONT : LABEL_FONT;

        Paragraph line = new Paragraph();
        line.setAlignment(Element.ALIGN_RIGHT);
        line.add(new Phrase(label + "  ", labelFont));
        line.add(new Phrase("₹" + value, font));
        document.add(line);
    }

    /**
     * Adds a header cell to the table with the specified background color.
     *
     * @param table      the PDF table
     * @param text       the cell text
     * @param background the background color
     * @throws DocumentException if the cell cannot be added
     */
    private void addTableHeaderCell(Table table, String text, Color background)
            throws DocumentException {
        Cell cell = new Cell(new Phrase(text, TABLE_HEADER_FONT));
        cell.setBackgroundColor(background);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    /**
     * Adds a data cell to the table with the specified background color.
     *
     * @param table      the PDF table
     * @param text       the cell text
     * @param background the background color
     * @throws DocumentException if the cell cannot be added
     */
    private void addTableDataCell(Table table, String text, Color background)
            throws DocumentException {
        Cell cell = new Cell(new Phrase(text, TABLE_CELL_FONT));
        cell.setBackgroundColor(background);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    /**
     * Formats a BigDecimal as a currency string with 2 decimal places.
     *
     * @param amount the monetary amount
     * @return the formatted string, or "0.00" if null
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
