package com.unicenta.poc.domain;

import com.unicenta.poc.interfaces.dto.SalesClosedPosReportItem;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;

@Repository
public interface SalesClosedPosRepository extends ListCrudRepository<SalesClosedPosReportItem, Long> {

    // Custom query method using explicit INNER JOINs via receipts table
    @Query("SELECT "
            + "c.HOST, "
            + "c.HOSTSEQUENCE, "
            + // Include HOSTSEQUENCE as per the .bs script
            "c.MONEY, "
            + "c.DATESTART, "
            + "c.DATEEND, "
            + "p.PAYMENT, "
            + "SUM(p.TOTAL) AS TOTAL "
            + "FROM closedcash c "
            + "INNER JOIN receipts r ON c.MONEY = r.MONEY "
            + "INNER JOIN payments p ON p.RECEIPT = r.ID "
            + "GROUP BY c.HOST, c.HOSTSEQUENCE, c.MONEY, c.DATESTART, c.DATEEND, p.PAYMENT "
            + // Added HOSTSEQUENCE to GROUP BY as per .bs script
            "ORDER BY c.HOST, c.HOSTSEQUENCE")
    List<SalesClosedPosReportItem> findClosedPosReportData(); // Method name doesn't matter much, @Query takes precedence

    // Optional: Query with date range filter using explicit INNER JOINs via receipts table
    @Query("SELECT "
            + "c.HOST, "
            + "c.HOSTSEQUENCE, "
            + // Include HOSTSEQUENCE as per the .bs script
            "c.MONEY, "
            + "c.DATESTART, "
            + "c.DATEEND, "
            + "p.PAYMENT, "
            + "SUM(p.TOTAL) AS TOTAL "
            + "FROM closedcash c "
            + "INNER JOIN receipts r ON c.MONEY = r.MONEY "
            + "INNER JOIN payments p ON p.RECEIPT = r.ID "
            + "WHERE c.DATESTART >= :startDate "
            + "AND c.DATEEND <= :endDate "
            + "GROUP BY c.HOST, c.HOSTSEQUENCE, c.MONEY, c.DATESTART, c.DATEEND, p.PAYMENT "
            + // Added HOSTSEQUENCE to GROUP BY as per .bs script
            "ORDER BY c.HOST, c.HOSTSEQUENCE") // Order by HOSTSEQUENCE as per .bs script
    List<SalesClosedPosReportItem> findClosedPosReportDataByDateRange(LocalDateTime startDate, LocalDateTime endDate);

}
