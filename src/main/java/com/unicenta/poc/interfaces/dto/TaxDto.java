package com.unicenta.poc.interfaces.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TaxDto {

    @NotBlank(message = "EL nombre es obligatorio")
    private String name;
    @NotBlank(message = "La categori del impuesto es obligatoria")
    private String taxcatId;
    @NotNull
    @PositiveOrZero(message = "La tase debse ser positiva o cero")
    private Double rate;
}
