package com.unicenta.poc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("LOCATIONS")
public class Location {

    @Id
    private String id;
    private String name;
    private String address;

    public Location(String name, String address) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
    }
}
