package com.unicenta.poc.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending product data to the client. Includes the category name.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private String id;
    private String reference;
    private String code;
    private String codetype;
    private String name;
    private double pricesell;
    private double pricebuy;
    private String categoryId;
    private String categoryName; 
    private String taxcatId;
    private String display;
    private double taxRate;
    private String taxName;
    private String idSupplier;
    private String supplierName;
}
