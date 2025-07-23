package com.unicenta.poc.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "taxes")
@Data
@NoArgsConstructor
public class Tax {

    @Id
    private String id;
    private String name;
    @Column(name = "CATEGORY")
    private String categoryId;
    private double rate;

    public Tax(String name, String categoryId, double rate) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.categoryId = categoryId;
        this.rate = rate;
    }
}
