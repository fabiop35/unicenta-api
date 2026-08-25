package com.unicenta.poc.interfaces.dto;

import lombok.Data;

@Data
public class InventoryItemValuationDto {

    private String productId;
    private String productName;
    private Double units;
    private Double costPrice;
    private String costSource;
    private Double invested;
    private Double itemValue;
    private Double priceSell;
    private Double retailValue;
    private Double potentialMargin;
    private String attributeSetInstanceId;
}
