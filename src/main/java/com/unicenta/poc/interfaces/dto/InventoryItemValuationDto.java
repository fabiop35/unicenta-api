package com.unicenta.poc.interfaces.dto;

import lombok.Data;

@Data
public class InventoryItemValuationDto {

    private String productId;
    private String productName;
    private Double units;
    private Double costPrice;
    private Double itemValue;
    private String attributeSetInstanceId;
}
