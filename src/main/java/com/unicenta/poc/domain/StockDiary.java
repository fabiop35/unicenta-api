package com.unicenta.poc.domain;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

@Data
@NoArgsConstructor
@Table("stockdiary")
public class StockDiary implements Persistable<String> {

    @Id
    private String id;
    @Column("datenew")
    private LocalDateTime date;
    @Column("reason")
    private int reason; // 0=Purchase, 1=Sale, 2=Adjustment, 3=Movement
    @Column("location")
    private String locationId;
    @Column("product")
    private String productId; // Foreign key to Product
    @Column("attributesetinstance_id")
    private String attributeSetInstanceId;
    private Double units;
    private Double price; // Cost price at time of movement
    @Column("appuser")
    private String userId;
    private String supplier; // Foreign key to Supplier.// Supplier ID for purchase movements
    @Column("supplierdoc")
    private String supplierDoc; // Supplier document reference
    @Transient
    private String notes;

    @Transient
    private String productName;

    @Transient
    private boolean isNew;

    @Transient
    private boolean isNewProduct = true;

    public StockDiary(LocalDateTime date, int reason, String locationId,
            String productId, String attributeSetInstanceId, Double units,
            Double price, String userId, String supplier, String supplierDoc, String notes) {

        this.id = java.util.UUID.randomUUID().toString();
        this.date = date;
        this.reason = reason;
        this.locationId = locationId;
        this.productId = productId;
        this.attributeSetInstanceId = attributeSetInstanceId;
        this.units = units;
        this.price = price;
        this.userId = userId;
        this.supplier = supplier;
        this.supplierDoc = supplierDoc;
        this.notes = notes;
    }

    @Override
    public boolean isNew() {
        return this.isNew || id == null;
    }

    public void markNotNew() {
        this.isNewProduct = false;
    }
}
