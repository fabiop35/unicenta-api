package com.unicenta.poc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("stockcurrent")
public class StockCurrent implements Persistable<String> {

    // Use a generated composite key as the ID for Spring Data JDBC
    @Id
    private String id; // Will be location + "_" + product + "_" + (attributesetinstance_id ?? "NULL")

    @Column("location")
    private String locationId;

    @Column("product")
    private String productId;

    @Column("attributesetinstance_id")
    private String attributeSetInstanceId;

    private Double units;

    @Transient
    private boolean isNew = true;

    public StockCurrent(String locationId, String productId, String attributeSetInstanceId, Double units) {
        this.locationId = locationId;
        this.productId = productId;
        this.attributeSetInstanceId = attributeSetInstanceId;
        this.units = units;
        this.id = generateId(locationId, productId, attributeSetInstanceId);
    }

    // Generate stable ID for Spring Data JDBC
    private String generateId(String locationId, String productId, String asiId) {
        return locationId + "_" + productId + "_" + (asiId != null ? asiId : "NULL");
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
