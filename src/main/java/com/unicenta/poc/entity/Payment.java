package com.unicenta.poc.entity;

import java.math.BigDecimal;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("payments")
public class Payment {

    @Column("id")
    private String id;

    @Column("receipt")
    private String receipt;

    @Column("payment")
    private String paymentType;

    @Column("total")
    private BigDecimal total;

    @Column("tip")
    private BigDecimal tip;

    @Column("transid")
    private String transId;

    @Column("isprocessed")
    private Boolean isProcessed;

    @Column("returnmsg")
    private byte[] returnMsg; // Using byte[] for BLOB

    @Column("notes")
    private String notes;

    @Column("tendered")
    private BigDecimal tendered;

    @Column("cardname")
    private String cardName;

    @Column("voucher")
    private String voucher;

    public Payment() {
    }

    public Payment(String id, String receipt, String paymentType, BigDecimal total, BigDecimal tip, String transId, Boolean isProcessed, byte[] returnMsg, String notes, BigDecimal tendered, String cardName, String voucher) {
        this.id = id;
        this.receipt = receipt;
        this.paymentType = paymentType;
        this.total = total;
        this.tip = tip;
        this.transId = transId;
        this.isProcessed = isProcessed;
        this.returnMsg = returnMsg;
        this.notes = notes;
        this.tendered = tendered;
        this.cardName = cardName;
        this.voucher = voucher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTip() {
        return tip;
    }

    public void setTip(BigDecimal tip) {
        this.tip = tip;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public Boolean getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }

    public byte[] getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(byte[] returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getTendered() {
        return tendered;
    }

    public void setTendered(BigDecimal tendered) {
        this.tendered = tendered;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }
}
