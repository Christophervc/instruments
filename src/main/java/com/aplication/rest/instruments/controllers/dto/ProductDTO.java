package com.aplication.rest.instruments.controllers.dto;


import com.aplication.rest.instruments.entities.Manufacturer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ProductDTO {

    private UUID id;
    @NotBlank(message = "El nombre no debe estar vacio")
    private String name;
    @NotBlank(message = "El tipo es obligatorio")
    private String type;
    @Size (min = 10, message = "La descripcion debe tener al menos 8 caracteres")
    private String description;
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value ="100.0", message = "El precio debe ser mayor a 100")
    private BigDecimal price;
    @NotNull(message = "El fabricante es obligatorio")
    private Manufacturer manufacturer;

}
