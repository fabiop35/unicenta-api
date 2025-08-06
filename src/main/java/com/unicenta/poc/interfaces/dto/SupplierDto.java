package com.unicenta.poc.interfaces.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SupplierDto {

    @NotBlank(message = "Search key is mandatory")
    private String searchkey;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotNull(message = "Max debt is mandatory")
    @PositiveOrZero
    private Double maxdebt;
    @Email(message = "Email should be valid")
    private String email;
    private String taxid;
    private String address;
    private String address2;
    private String postal;
    private String city;
    private String region;
    private String country;
    private String firstname;
    private String lastname;
    private String phone;
    private String phone2;
    private String fax;
    private String notes;
    private boolean visible = true;
    private LocalDateTime curdate;
    @PositiveOrZero
    private Double curdebt = 0.0;
    private String vatid;
}
