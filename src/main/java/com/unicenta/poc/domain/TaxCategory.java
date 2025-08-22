package com.unicenta.poc.domain;

import java.util.UUID;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "taxcategories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxCategory implements Persistable<String> {

    @Id
    private String id;
    private String name;

//    @JsonIgnore
//    private String value = UUID.randomUUID().toString();

    @Transient
    private boolean isNewProduct = true;

    public TaxCategory(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return isNewProduct;
    }

    public void markNotNew() {
        this.isNewProduct = false;
    }
}
