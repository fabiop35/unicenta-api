package com.unicenta.poc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("STOCKCURRENT")
public class StockCurrent {

    @Column("LOCATION")
    private String locationId;

    @Column("PRODUCT")
    private String productId;

    @Column("ATTRIBUTESETINSTANCE_ID")
    private String attributeSetInstanceId;

    private Double units;

    public StockCurrent(String locationId, String productId, String attributeSetInstanceId, Double units) {
        this.locationId = locationId;
        this.productId = productId;
        this.attributeSetInstanceId = attributeSetInstanceId;
        this.units = units;
    }
}