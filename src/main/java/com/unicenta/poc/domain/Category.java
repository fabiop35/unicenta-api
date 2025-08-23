package com.unicenta.poc.domain;

import java.util.UUID;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore; 

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category implements Persistable<String> {

    @Id
    private String id;
    private String name;
    
    /*@JsonIgnore
    UUID value = UUID.randomUUID();*/

    @Transient
    private boolean isNewProduct = true;

    public Category(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.isNewProduct = true;
    }
     public Category(String id, String name) {
        this.id = id;
        this.name = name;
        this.isNewProduct = true;
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
