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
    private String name;
    private double pricesell;
    private double pricebuy;
    private String categoryId;
    private String categoryName; // <-- The new field with the category name
    private String taxcatId;
    private String display;
}
