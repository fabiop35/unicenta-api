package com.unicenta.poc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("stockcurrent")
public class StockCurrent {

    @Column("location")
    private String locationId;

    @Column("product")
    private String productId;

    @Column("attributesetinstance_id")
    private String attributeSetInstanceId;

    private Double units;

    public StockCurrent(String locationId, String productId, String attributeSetInstanceId, Double units) {
        this.locationId = locationId;
        this.productId = productId;
        this.attributeSetInstanceId = attributeSetInstanceId;
        this.units = units;
    }
}