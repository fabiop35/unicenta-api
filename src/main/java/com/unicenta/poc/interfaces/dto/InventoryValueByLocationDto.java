package com.unicenta.poc.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryValueByLocationDto {

    private String locationId;
    private String locationName;
    private Double totalValue;
    private Long itemCount;
    private Long productCount;
}
