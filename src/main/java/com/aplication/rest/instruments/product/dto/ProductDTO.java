package com.aplication.rest.instruments.product.dto;


import com.aplication.rest.instruments.manufacturer.Manufacturer;

import com.aplication.rest.instruments.product.enums.InstrumentType;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "El tipo de instrumento es obligatorio")
    private InstrumentType type;

    @Size (min = 10, message = "La descripcion debe tener al menos 8 caracteres")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value ="100.0", message = "El precio debe ser mayor a 100")
    private BigDecimal price;

    private String slug;

    private String sku;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private Boolean active;

    private String imageUrl;

    @NotNull(message = "El fabricante es obligatorio")
    private Manufacturer manufacturer;

}
