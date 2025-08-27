package com.unicenta.poc.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "products")
@Data
@NoArgsConstructor
public class Product implements Persistable<String> {

    @Id
    private String id;
    private String reference;
    private String code;
    private String name;
    private double pricesell;
    private double pricebuy;

    @Column("category")
    private String categoryId;

    @Column("taxcat")
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
    
    @Transient
    Date now = new Date();
    Timestamp memodate = new Timestamp(now.getTime());
    
    @Transient
    UUID value = UUID.randomUUID();
    
    /*@Column("category_id")
    UUID categoryIdNotKey = UUID.randomUUID();*/
    
    @Transient
    String currency = "NDF";
    
    /*@Column("tax_category_id")
    String taxCategoryId = "NULL";*/
    
    @Column("supplier")
    private String idSupplier;
    
    @Transient // This field will NOT be mapped to a database column
    private boolean isNewProduct = true;
    
    

    public Product(String reference, String code, String name, double pricesell, double pricebuy,
            String categoryId, String taxcatId, String display, String idSupplier) {
        
        this.id = UUID.randomUUID().toString();
        this.reference = reference;
        this.code = code;
        this.name = name;
        this.pricesell = pricesell;
        this.pricebuy = pricebuy;
        this.categoryId = categoryId;
        this.taxcatId = taxcatId;
        this.display = display;
        this.idSupplier = idSupplier;
        
        this.isNewProduct = true;
    }
    
    @Override
    public boolean isNew() {
        // Return true if this is a newly created entity, false if loaded from DB
        return isNewProduct;
    }
    
    public void markNotNew() {
        this.isNewProduct = false;
    }
}
