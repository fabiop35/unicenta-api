package com.unicenta.poc.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "taxcategories")
@Data
@NoArgsConstructor
public class TaxCategory {
    @Id
    private String id;
    private String name;

    public TaxCategory(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
}