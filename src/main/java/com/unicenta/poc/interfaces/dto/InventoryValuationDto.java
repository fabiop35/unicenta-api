package com.unicenta.poc.interfaces.dto;

import lombok.Data;
import java.util.List;

@Data
public class InventoryValuationDto {

    private double totalValue;
    private List<InventoryItemValuationDto> items;
}
