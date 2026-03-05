package com.aplication.rest.instruments.product.dto;

import com.aplication.rest.instruments.manufacturer.dto.ManufacturerDTO;
import com.aplication.rest.instruments.product.enums.InstrumentType;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProductDTO(
        //@java.io.Serial private static final long serialVersionUID =1L;
        UUID id,

        @NotBlank(message = "{product.name.required}")
        String name,

        @NotNull(message = "{product.type.required}")
        InstrumentType type,

        @Size(min = 10, message = "{product.description.size}")
        String description,

        @NotNull(message = "{product.price.required}") @DecimalMin(value = "100.0", message = "{product.price.min}")
        BigDecimal price,

        String slug,

        String sku,

        @NotNull(message = "{product.stock.required}")
        @Min(value = 1, message = "{product.stock.min}")
        Integer stock,

        Boolean active,

        String image_url,

        @NotNull(message = "{product.manufacturer.required}")
        ManufacturerDTO manufacturer
)
        implements Serializable {
}
