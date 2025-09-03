package com.unicenta.poc.domain;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
@EqualsAndHashCode
public class StockCurrentId implements Serializable {

    @Column("location")
    private String locationId;

    @Column("product")
    private String productId;

    @Column("attributesetinstance_id")
    private String attributeSetInstanceId;

    public StockCurrentId() {
    }

    public StockCurrentId(String locationId, String productId, String attributeSetInstanceId) {
        this.locationId = locationId;
        this.productId = productId;
        this.attributeSetInstanceId = attributeSetInstanceId;
    }
}
