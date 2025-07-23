package com.unicenta.poc.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NameDto {

    @NotBlank(message = "Name is mandatory")
    private String name;
}
