package com.unicenta.poc.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NameDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
}
