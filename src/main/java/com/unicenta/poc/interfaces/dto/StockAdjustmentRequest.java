package com.unicenta.poc.interfaces.dto;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class StockAdjustmentRequest {

    @NotBlank(message = "Location ID is required")
    private String locationId;

    @NotBlank(message = "Product ID is required")
    private String productId;

    private String attributeSetInstanceId;

    @NotNull(message = "New stock value is required")
    @DecimalMin(value = "0.0", message = "Stock cannot be negative")
    private Double newStock;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String notes;
}
