package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.product.dto.ProductSearchCriteria;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IProductService {

    Result<Page<ProductDTO>> findAll(Pageable pageable, ProductSearchCriteria criteria);

    Result<ProductDTO> findById(UUID id);

    Result<ProductDTO> save(ProductDTO productDTO, MultipartFile file);

    Result<ProductDTO> deleteById(UUID id);

    Result<ProductDTO> update(UUID id, ProductDTO productDTO);

    Result<ProductDTO> findBySku(String sku);

    void exportProductsToExcel(HttpServletResponse response);

    Result<ProductDTO> reduceStock(UUID id, Integer quantity);

    Result<ProductDTO> addStock(UUID id, Integer quantity);

    Result<ProductDTO> uploadImage(UUID id, MultipartFile file);
}
