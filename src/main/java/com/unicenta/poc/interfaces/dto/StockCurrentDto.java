package com.unicenta.poc.interfaces.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class StockCurrentDto {

    private String locationId;
    private String productId;
    private String attributeSetInstanceId;
    private Double units;
    private String productName;
    private String locationName;
    private String productReference;
    private String productCode;
    private String attributeSetInstanceDescription;
    private double pricebuy;
    private String idSupplier;
    private double pricesell;
}
