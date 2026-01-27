package com.aplication.rest.instruments.controllers.dto;

import com.aplication.rest.instruments.entities.Product;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ManufacturerDTO {

    private UUID id;

    @NotBlank(message = "El nombre del fabricante es obligatorio")
    private String name;

    private List<Product> productList = new ArrayList<>();
}
