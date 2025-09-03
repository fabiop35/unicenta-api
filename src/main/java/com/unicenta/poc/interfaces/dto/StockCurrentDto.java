package com.unicenta.poc.interfaces.dto;

import lombok.Data;

@Data
public class StockCurrentDto {

    private String locationId;
    private String productId;
    private String attributeSetInstanceId;
    private Double units;
    private String productName;
    private String locationName;
    private String attributeSetInstanceDescription;
    private String productReference;
    private String productCode;
}
