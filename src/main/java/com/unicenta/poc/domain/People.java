package com.unicenta.poc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("PEOPLE")
public class People {

    @Id
    private String id;
    private String name;
    private String apppassword;
    private String card;
    private String role;
    private boolean visible;
    private byte[] image; // Mediumblob in MySQL maps to byte[] in Java

    public People(String id, String name, String apppassword, String card,
            String role, boolean visible, byte[] image) {
        this.id = id;
        this.name = name;
        this.apppassword = apppassword;
        this.card = card;
        this.role = role;
        this.visible = visible;
        this.image = image;
    }
}
