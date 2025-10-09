package com.unicenta.poc.web.rest;

import com.unicenta.poc.application.SalesClosedPosService;
import com.unicenta.poc.interfaces.dto.SalesClosedPosReportItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales/sales-closed-pos")
@CrossOrigin(origins = "*")
public class SalesClosedPosController {

    @Autowired
    private SalesClosedPosService service;

    // Define the expected date format
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @GetMapping
    public ResponseEntity<List<SalesClosedPosReportItem>> getReportData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<SalesClosedPosReportItem> reportItems;
        try {
            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = LocalDateTime.parse(startDate, DATE_FORMATTER);
                LocalDateTime endDateTime = LocalDateTime.parse(endDate, DATE_FORMATTER);
                reportItems = service.getReportDataByDateRange(startDateTime, endDateTime);
            } else {
                reportItems = service.getReportData();
            }
            return ResponseEntity.ok(reportItems);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReportAsPdf(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws IOException {

        try {
            List<SalesClosedPosReportItem> reportItems;
            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = LocalDateTime.parse(startDate, DATE_FORMATTER);
                LocalDateTime endDateTime = LocalDateTime.parse(endDate, DATE_FORMATTER);
                reportItems = service.getReportDataByDateRange(startDateTime, endDateTime);
            } else {
                reportItems = service.getReportData();
            }

            byte[] pdfBytes = service.generatePdfReport(reportItems);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "sales_closed_pos_report.pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date for PDF: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
