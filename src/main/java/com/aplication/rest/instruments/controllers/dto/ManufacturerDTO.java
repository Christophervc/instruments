package com.aplication.rest.instruments.controllers.dto;

import com.aplication.rest.instruments.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ManufacturerDTO {

    private Long id;

    private String name;

    private List<Product> productList = new ArrayList<>();
}
