package com.unicenta.poc.domain;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {

    @Id
    private String id;
    private String reference;
    private String code;
    private String name;
    private double pricesell;
    private double pricebuy;

    @Column(name = "CATEGORY")
    private String categoryId;

    @Column(name = "TAXCAT")
    private String taxcatId;

    private boolean iscom = false;
    private boolean isscale = false;
    private String display;

    // Default values for other fields
    private Double stockcost = 0.0;
    private Double stockvolume = 0.0;
    private Double stockunits = 0.0;
    private boolean isvprice = false;
    private String codetype = "EAN-13";

    private Double warranty = 0.0;
    private Double isverpatrib = 0.0;
    private int printto = 1;
    private int uom = 0;

    Date now = new Date();
    Timestamp memodate = new Timestamp(now.getTime());
    UUID value = UUID.randomUUID();
    @Column(name = "CATEGORY_ID")
    UUID categoryIdNotKey = UUID.randomUUID();
    String currency = "NDF";
    @Column(name = "tax_category_id")
    String taxCategoryId = "NULL";

    public Product(String reference, String code, String name, double pricesell, double pricebuy,
            String categoryId, String taxcatId, String display) {
        this.id = UUID.randomUUID().toString();
        this.reference = reference;
        this.code = code;
        this.name = name;
        this.pricesell = pricesell;
        this.pricebuy = pricebuy;
        this.categoryId = categoryId;
        this.taxcatId = taxcatId;
        this.display = display;
    }
}
