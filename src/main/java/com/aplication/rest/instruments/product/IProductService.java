package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.product.dto.ProductSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IProductService {

    Result<Page<ProductDTO>> findAll(Pageable pageable, ProductSearchCriteria criteria);

    Result<Optional<ProductDTO>> findById(UUID id);

    Result<ProductDTO> save(ProductDTO productDTO);

    Result<ProductDTO> deleteById(UUID id);

    Result<ProductDTO> update(UUID id, ProductDTO productDTO);
}
