package com.unicenta.poc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Projection carrying the derived cost basis of a product computed from its
 * priced stock-in history in stockdiary: weighted average unit cost and
 * lifetime invested amount.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCostBasis {

    private String productId;
    private Double avgCost;
    private Double invested;
}
