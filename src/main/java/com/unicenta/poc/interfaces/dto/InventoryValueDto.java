package com.unicenta.poc.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InventoryValueDto {

    private Double totalValue;
    private Long itemCount;
    private Long productCount;
    private LocalDateTime asOf;
    private List<InventoryValueByLocationDto> breakdowns;
}
