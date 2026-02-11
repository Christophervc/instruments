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

    @NotBlank(message = "{product.name.required}")
    private String name;

    @NotNull(message = "{product.type.required}")
    private InstrumentType type;

    @Size (min = 10, message = "{product.description.size}")
    private String description;

    @NotNull(message = "{product.price.required}")
    @DecimalMin(value ="100.0", message = "{product.price.min}")
    private BigDecimal price;

    private String slug;

    @Size(min = 3, message = "{product.sku.min}")
    private String sku;

    @NotNull(message = "{product.stock.required}")
    @Min(value = 1, message = "{product.stock.min}")
    private Integer stock;

    private Boolean active;

    private String image_url;

    @NotNull(message = "{product.manufacturer.required}")
    private Manufacturer manufacturer;

}
