package com.unicenta.poc.interfaces.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Column;

public class SalesClosedPosReportItem {

    @Column("HOST")
    private String host;

    @Column("HOSTSEQUENCE")
    private Integer hostSequence;

    @Column("MONEY")
    private String money;

    @Column("DATESTART")
    private LocalDateTime dateStart;

    @Column("DATEEND")
    private LocalDateTime dateEnd;

    @Column("PAYMENT")
    private String payment;

    @Column("TOTAL")
    private BigDecimal total;

    public SalesClosedPosReportItem() {
    }

    // Constructor matching the SELECT fields from the query
    public SalesClosedPosReportItem(String host, Integer hostSequence, String money, LocalDateTime dateStart, LocalDateTime dateEnd, String payment, BigDecimal total) {
        this.host = host;
        this.hostSequence = hostSequence;
        this.money = money;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.payment = payment;
        this.total = total;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getHostSequence() {
        return hostSequence;
    }

    public void setHostSequence(Integer hostSequence) {
        this.hostSequence = hostSequence;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDateTime getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDateTime dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
