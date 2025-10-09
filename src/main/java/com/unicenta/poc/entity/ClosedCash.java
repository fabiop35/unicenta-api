package com.unicenta.poc.entity;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("closedcash")
public class ClosedCash {

    @Column("money")
    private String money;

    @Column("host")
    private String host;

    @Column("hostsequence")
    private Integer hostSequence;

    @Column("datestart")
    private LocalDateTime dateStart;

    @Column("dateend")
    private LocalDateTime dateEnd;

    @Column("nosales")
    private Integer noSales;

    public ClosedCash() {
    }

    public ClosedCash(String money, String host, Integer hostSequence, LocalDateTime dateStart, LocalDateTime dateEnd, Integer noSales) {
        this.money = money;
        this.host = host;
        this.hostSequence = hostSequence;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.noSales = noSales;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
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

    public Integer getNoSales() {
        return noSales;
    }

    public void setNoSales(Integer noSales) {
        this.noSales = noSales;
    }
}
