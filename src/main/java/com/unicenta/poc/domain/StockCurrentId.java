package com.unicenta.poc.domain;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@EqualsAndHashCode
public class StockCurrentId implements Serializable {
    private final String locationId;
    private final String productId;
    private final String attributeSetInstanceId;

    public StockCurrentId(String locationId, String productId, String attributeSetInstanceId) {
        this.locationId = locationId;
        this.productId = productId;
        this.attributeSetInstanceId = attributeSetInstanceId;
    }

    // Required by some serializers
    public StockCurrentId() {
        this(null, null, null);
    }
}