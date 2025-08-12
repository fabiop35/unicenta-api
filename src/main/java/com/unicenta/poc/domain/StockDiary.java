package com.unicenta.poc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Table("stockdiary")
@Data
@NoArgsConstructor
public class StockDiary implements Persistable<String> {

    @Id
    private String id;
    private LocalDateTime datenew;
    private int reason;
    private String location;
    private String product; // Foreign key to Product
    private String attributesetinstanceId;
    private double units;
    private double price;
    private String appuser;
    private String supplier; // Foreign key to Supplier
    private String supplierdoc;

    @Transient
    private String productName;

    @Transient
    private boolean isNew;

    @Transient
    private boolean isNewProduct = true;

    @Override
    public boolean isNew() {
        return this.isNew || id == null;
    }

    public void markNotNew() {
        this.isNewProduct = false;
    }
}
