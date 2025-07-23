package com.unicenta.poc.interfaces.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TaxDto {

    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Category ID is mandatory")
    private String categoryId;
    @NotNull
    @PositiveOrZero(message = "Rate must be positive or zero")
    private Double rate;
}
