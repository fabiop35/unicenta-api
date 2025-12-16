package com.unicenta.poc.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "TAXES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tax implements Persistable<String> {

    @Id
    private String id;
    private String name;
    
    @Column("CATEGORY")
    private String taxcatId;
    private double rate;
    //UUID value = UUID.randomUUID();
    
    @Transient
    private boolean isNewProduct = true;

    public Tax(String name, String taxcatId, double rate) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.taxcatId = taxcatId;
        this.rate = rate;
    }

    @Override
    public boolean isNew() {
        return isNewProduct;
    }

    public void markNotNew() {
        this.isNewProduct = false;
    }
}
