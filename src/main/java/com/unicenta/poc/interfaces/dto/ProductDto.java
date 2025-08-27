package com.unicenta.poc.interfaces.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductDto {

    @NotBlank(message = "Reference is mandatory")
    private String reference;
    
    @NotBlank(message = "Code is mandatory")
    private String code;
    
    @NotBlank(message = "Codetype is mandatory")
    private String codetype;
    
    @NotBlank(message = "Name is mandatory")
    private String name;
    
    @NotNull
    @Positive(message = "Sell price must be positive")
    private Double pricesell;
    
    @NotNull
    @PositiveOrZero(message = "Buy price must be positive or zero")
    private Double pricebuy;
    
    @NotBlank(message = "Category ID is mandatory")
    private String categoryId;
    
    @NotBlank(message = "Tax Category ID is mandatory")
    private String taxcatId;
    
    //@NotBlank(message = "Display name is mandatory")
    private String display;
    
    private String idSupplier;
    private String supplierName;
}
