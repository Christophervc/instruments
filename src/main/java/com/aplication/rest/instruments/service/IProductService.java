package com.aplication.rest.instruments.service;

import com.aplication.rest.instruments.controllers.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.entities.Manufacturer;
import com.aplication.rest.instruments.entities.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProductService {

    Result<List<ProductDTO>> findAll();

    Result<Optional<ProductDTO>> findById(UUID id);

    Result<ProductDTO> save(ProductDTO productDTO);

    Result<ProductDTO> deleteById(UUID id);

    Result<ProductDTO> update(UUID id, ProductDTO productDTO);

    /*
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByManufacturer(Manufacturer manufacturer);

    List<Product> findAllSortedByNameAsc();

    List<Product> findAllSortedByNameDesc();
    */
}
