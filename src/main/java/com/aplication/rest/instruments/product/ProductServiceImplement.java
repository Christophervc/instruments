package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.product.dto.ProductSearchCriteria;
import com.aplication.rest.instruments.product.utils.ProductExcelExporter;
import com.aplication.rest.instruments.product.utils.ProductHelper;
import com.aplication.rest.instruments.product.utils.ProductSpecification;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


@Service
public class ProductServiceImplement implements IProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductHelper productHelper;

    @Override
    public Result<Page<ProductDTO>> findAll(Pageable pageable, ProductSearchCriteria criteria) {
        try {
            Specification<Product> spec = ProductSpecification.fromCriteria(criteria);
            Page<Product> productsPage = productRepository.findAll(spec, pageable);
            Page<ProductDTO> dtoPage = productsPage.map(productMapper::toDTO);
            return Result.success(dtoPage);
        } catch (Exception e) {
            return Result.isFailure(new ApiError("DATABASE_ERROR", "ERROR RETRIEVING PRODUCTS"));
        }
    }

    @Override
    public Result<Optional<ProductDTO>> findById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        ProductDTO productDTO = productMapper.toDTO(product);
        return Result.success(Optional.of(productDTO));
    }

    @Override
    public Result<ProductDTO> save(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);

        if (product.getSku() == null || product.getSku().isBlank()) {
            product.setSku(productHelper.generateSku(product));
        }
        product.setSlug(productHelper.generateSlug(product.getName()));
        if (product.getStock() == null) {
            product.setStock(0);
        }

        Product savedProduct = productRepository.save(product);
        ProductDTO savedProductDTO = productMapper.toDTO(savedProduct);
        return Result.success(savedProductDTO);
    }

    @Override
    public Result<ProductDTO> update(UUID id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        productMapper.updateProductFromDTO(productDTO, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return Result.success(productMapper.toDTO(updatedProduct));
    }

    @Override
    public Result<ProductDTO> findBySku(String sku) {
        Product existingProduct = productRepository.findBySku(sku)
                .orElseThrow(()-> new NotFoundException("Product not found with sku:" + sku));
        ProductDTO productDTO = productMapper.toDTO(existingProduct);
        return Result.success(productDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public void exportProductsToExcel(HttpServletResponse response) {
        // Config response header
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=products_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        try (Stream<Product> products = productRepository.streamAll()) {
            ProductExcelExporter exporter = new ProductExcelExporter(products);
            exporter.export(response);
        } catch (IOException e) {
            throw new RuntimeException("Error exporting Excel", e);
        }

    }

    @Override
    @Transactional
    public Result<ProductDTO> deleteById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        product.setActive(false);
        Product deletedProduct = productRepository.save(product);
        ProductDTO productDTO = productMapper.toDTO(deletedProduct);
        return Result.success(productDTO);
    }

}
