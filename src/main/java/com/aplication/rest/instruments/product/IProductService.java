package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProductService {

    Result<List<ProductDTO>> findAll();

    Result<Optional<ProductDTO>> findById(UUID id);

    Result<ProductDTO> save(ProductDTO productDTO);

    Result<ProductDTO> deleteById(UUID id);

    Result<ProductDTO> update(UUID id, ProductDTO productDTO);
}
