package com.unicenta.poc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("ATTRIBUTESETINSTANCE")
public class AttributeSetInstance {

    @Id
    private String id;

    @Column("ATTRIBUTESET_ID")
    private String attributeSetId;

    private String description;

    public AttributeSetInstance(String id, String attributeSetId, String description) {
        this.id = id;
        this.attributeSetId = attributeSetId;
        this.description = description;
    }
}
