package com.unicenta.poc.interfaces.dto;

import lombok.Data;

/**
 * DTO for sending product data to the client. Includes the category name.
 */
@Data
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
}
