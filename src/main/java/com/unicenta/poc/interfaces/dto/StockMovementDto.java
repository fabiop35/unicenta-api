package com.unicenta.poc.interfaces.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockMovementDto {

    @NotNull
    private LocalDateTime date;

    @Min(0)
    @Max(3)
    private int reason; // 0=Purchase, 1=Sale, 2=Adjustment, 3=Movement

    @NotBlank
    private String locationId;

    @NotBlank
    private String productId;

    private String attributeSetInstanceId;

    @NotNull
    @DecimalMin(value = "-999999999.99", inclusive = false)
    @DecimalMax("999999999.99")
    private Double units;

    @NotNull
    @DecimalMin("0.0")
    private Double price;

    private String userId;

    private String supplierId;
    private String supplierDoc;

    private String notes;
}