package com.aplication.rest.instruments.controllers.dto;


import com.aplication.rest.instruments.entities.Manufacturer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ProductDTO {

    private Long id;

    private String name;

    private String type;

    private String description;

    private BigDecimal price;

    private Manufacturer manufacturer;

}
