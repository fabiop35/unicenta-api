package com.unicenta.poc.interfaces.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record StockEntryRequest(
        @NotBlank
        String productId,
        String attributeSetInstanceId,
        @NotNull
        @DecimalMin("0.0")
        Double price,
        @NotNull
        LocalDateTime date,
        @NotNull
        Integer reason,
        @NotBlank
        String locationId,
        String supplier,
        String supplierDoc,
        @NotNull
        @DecimalMin("0.01")
        Double units) {

}
