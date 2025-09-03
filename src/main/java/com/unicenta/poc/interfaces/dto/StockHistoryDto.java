package com.unicenta.poc.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockHistoryDto {

    private String id;
    private LocalDateTime date;
    private int reason;
    private String locationId;
    private String productId;
    private String attributeSetInstanceId;
    private Double units;
    private Double price;
    private String userId;
    private String supplierId;
    private String supplierDoc;
    private String notes;
    private String productName;
    private String locationName;
    private String userName;
    private String supplierName;
    private String attributeSetInstanceDescription;
}
