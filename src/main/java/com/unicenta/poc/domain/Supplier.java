package com.unicenta.poc.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("suppliers")
@Data
@NoArgsConstructor
public class Supplier implements Persistable<String> {

    @Id
    private String id;
    private String searchkey;
    private String taxid;
    private String name;
    private double maxdebt;
    private String address;
    private String address2;
    private String postal;
    private String city;
    private String region;
    private String country;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String phone2;
    private String fax;
    private String notes;
    private boolean visible = true;
    private LocalDateTime curdate;
    private Double curdebt = 0.0;
    private String vatid;

    @Transient
    private boolean isNew;

    @Transient
    private boolean isNewProduct = true;

    public Supplier(String searchkey, String name, double maxdebt) {
        this.id = UUID.randomUUID().toString();
        this.searchkey = searchkey;
        this.name = name;
        this.maxdebt = maxdebt;
        this.isNew = true;
        this.isNewProduct = true;
    }

    @Override
    public boolean isNew() {
        return this.isNew || id == null;
    }

    public void markNotNew() {
        this.isNewProduct = false;
    }
}
