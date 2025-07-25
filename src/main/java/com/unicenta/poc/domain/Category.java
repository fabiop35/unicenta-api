package com.unicenta.poc.domain;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "categories")
@Data
@NoArgsConstructor
public class Category implements Persistable<String> {

    @Id
    private String id;
    private String name;
    UUID value = UUID.randomUUID();

    @Transient
    private boolean isNewProduct = true;

    public Category(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.isNewProduct = true;
    }

    @Override
    public boolean isNew() {
        return isNewProduct;
    }

    public void markNotNew() {
        this.isNewProduct = false;
    }
}
