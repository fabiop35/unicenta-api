package com.unicenta.poc.domain;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "taxcategories")
@Data
@NoArgsConstructor
public class TaxCategory implements Persistable<String> {

    @Id
    private String id;
    private String name;
    private String value = UUID.randomUUID().toString();

    @Transient
    private boolean isNewProduct = true;

    public TaxCategory(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    @Override
    public boolean isNew() {
        return isNewProduct;
    }

    public void markNotNew() {
        this.isNewProduct = false;
    }
}
