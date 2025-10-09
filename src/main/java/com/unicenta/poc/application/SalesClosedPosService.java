package com.unicenta.poc.application;

import com.itextpdf.kernel.colors.ColorConstants;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.unicenta.poc.domain.SalesClosedPosRepository;
import com.unicenta.poc.interfaces.dto.SalesClosedPosReportItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.math.BigDecimal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
public class SalesClosedPosService {

    @Autowired
    private SalesClosedPosRepository repository;

    public List<SalesClosedPosReportItem> getReportData() {
        return repository.findClosedPosReportData();
    }

    public List<SalesClosedPosReportItem> getReportDataByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findClosedPosReportDataByDateRange(startDate, endDate);
    }

    public byte[] generatePdfReport(List<SalesClosedPosReportItem> reportItems) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        PdfFont normalFont = PdfFontFactory.createFont();
        PdfFont boldFont = PdfFontFactory.createFont();

        // Define Number Format for Colombian Pesos ---
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.'); // Thousands separator
        symbols.setDecimalSeparator(',');  // Decimal separator
        DecimalFormat numberFormat = new DecimalFormat("#,##0.00", symbols); // Format with 2 decimals and separators

        // Add Title
        Paragraph title = new Paragraph("Reporte de Cierres de Caja")
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Add current date/time
        Paragraph generatedOn = new Paragraph("Fecha de creación: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(generatedOn);

        document.add(new Paragraph("\n"));

        // Create table header
        Table table = new Table(new float[]{2, 1, 2, 2, 2, 2});

        // --- Create styled header cells ---
        Cell headerHost = new Cell().add(new Paragraph("Host").setFont(boldFont).setFontColor(ColorConstants.WHITE)); // Set font color
        headerHost.setBackgroundColor(ColorConstants.BLUE);
        headerHost.setTextAlignment(TextAlignment.CENTER);

        Cell headerSeq = new Cell().add(new Paragraph("Seq").setFont(boldFont).setFontColor(ColorConstants.WHITE));
        headerSeq.setBackgroundColor(ColorConstants.BLUE);
        headerSeq.setTextAlignment(TextAlignment.CENTER);

        Cell headerMoney = new Cell().add(new Paragraph("Money").setFont(boldFont).setFontColor(ColorConstants.WHITE));
        headerMoney.setBackgroundColor(ColorConstants.BLUE);
        headerMoney.setTextAlignment(TextAlignment.CENTER);

        Cell headerDateStart = new Cell().add(new Paragraph("Start Date").setFont(boldFont).setFontColor(ColorConstants.WHITE));
        headerDateStart.setBackgroundColor(ColorConstants.BLUE);
        headerDateStart.setTextAlignment(TextAlignment.CENTER);

        Cell headerDateEnd = new Cell().add(new Paragraph("End Date").setFont(boldFont).setFontColor(ColorConstants.WHITE));
        headerDateEnd.setBackgroundColor(ColorConstants.BLUE);
        headerDateEnd.setTextAlignment(TextAlignment.CENTER);

        Cell headerPayment = new Cell().add(new Paragraph("Payment Type").setFont(boldFont).setFontColor(ColorConstants.WHITE));
        headerPayment.setBackgroundColor(ColorConstants.BLUE);
        headerPayment.setTextAlignment(TextAlignment.CENTER);

        Cell headerTotal = new Cell().add(new Paragraph("Total").setFont(boldFont).setFontColor(ColorConstants.WHITE));
        headerTotal.setBackgroundColor(ColorConstants.BLUE);
        headerTotal.setTextAlignment(TextAlignment.CENTER);

        // Add the styled header cells to the table
        table.addHeaderCell(headerHost);
        table.addHeaderCell(headerSeq);
        //table.addHeaderCell(headerMoney);
        table.addHeaderCell(headerDateStart);
        table.addHeaderCell(headerDateEnd);
        table.addHeaderCell(headerPayment);
        table.addHeaderCell(headerTotal);

        // Add data rows with formatted numbers
        for (SalesClosedPosReportItem item : reportItems) {
            table.addCell(new Cell().add(new Paragraph(item.getHost() != null ? item.getHost() : "").setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(item.getHostSequence() != null ? item.getHostSequence().toString() : "").setFont(normalFont)));
            //table.addCell(new Cell().add(new Paragraph(item.getMoney() != null ? item.getMoney() : "").setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(item.getDateStart() != null ? item.getDateStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "").setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(item.getDateEnd() != null ? item.getDateEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "").setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(item.getPayment() != null ? item.getPayment() : "").setFont(normalFont)));
            // --- FORMAT TOTAL ---
            table.addCell(new Cell().add(new Paragraph(item.getTotal() != null ? numberFormat.format(item.getTotal()) : "0,00").setFont(normalFont)));
        }

        document.add(table);

        // Calculate and add totals (optional, similar to Jasper variables)
        Map<String, BigDecimal> totalsByHost = reportItems.stream()
                .collect(Collectors.groupingBy(
                        SalesClosedPosReportItem::getHost,
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, SalesClosedPosReportItem::getTotal, BigDecimal::add)
                ));

        document.add(new Paragraph("\n"));

        for (Map.Entry<String, BigDecimal> hostTotalEntry : totalsByHost.entrySet()) {
            String host = hostTotalEntry.getKey();
            BigDecimal hostTotal = hostTotalEntry.getValue();
            // --- FORMAT HOST TOTAL ---
            Paragraph hostTotalPara = new Paragraph("Total para terminal " + host + ": " + numberFormat.format(hostTotal))
                    .setFont(boldFont)
                    .setBold();

            document.add(hostTotalPara);
        }

        // --- FORMAT GRAND TOTAL ---
        BigDecimal grandTotal = reportItems.stream()
                .map(SalesClosedPosReportItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Paragraph grandTotalPara = new Paragraph("\nGran Total: " + numberFormat.format(grandTotal))
                .setFont(boldFont)
                .setBold();
        document.add(grandTotalPara);

        document.close();
        return outputStream.toByteArray();
    }
}
