package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.manufacturer.Manufacturer;
import com.aplication.rest.instruments.manufacturer.ManufacturerRepository;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.product.dto.ProductSearchCriteria;
import com.aplication.rest.instruments.product.utils.ProductExcelExporter;
import com.aplication.rest.instruments.product.utils.ProductHelper;
import com.aplication.rest.instruments.product.utils.ProductSpecification;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private ManufacturerRepository manufacturerRepository;
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

    @Cacheable(value = "products", key = "#id")
    @Override
    public Result<ProductDTO> findById(UUID id) {
        // try { Thread.sleep(2000); } catch (InterruptedException e) {}
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        ProductDTO productDTO = productMapper.toDTO(product);
        return Result.success(productDTO);
    }

    @Override
    @Transactional
    public Result<ProductDTO> save(ProductDTO productDTO) {
        if (productDTO.getManufacturer() == null || productDTO.getManufacturer().getId() == null) {
            throw new ValidationException("Manufacturer is mandatory");
        }
        Product product = productMapper.toEntity(productDTO);

        UUID manufacturerId = productDTO.getManufacturer().getId();
        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new NotFoundException("Manufacturer not found with id: " + manufacturerId));
        product.setManufacturer(manufacturer);

        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        product.setSlug(productHelper.generateSlug(product.getName(), product.getId()));

        if (productDTO.getSku() != null) {
            Optional<Product> existingSku = productRepository.findBySku(productDTO.getSku());
            if (existingSku.isPresent()) {
                throw new ValidationException("SKU " + productDTO.getSku() + " already exists ");
            }
            product.setSku(productDTO.getSku());
        }
        product.setActive(true);
        Product savedProduct = productRepository.save(product);
        ProductDTO savedProductDTO = productMapper.toDTO(savedProduct);
        return Result.success(savedProductDTO);
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public Result<ProductDTO> update(UUID id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        if (productDTO.getName() != null && !productDTO.getName().equals(existingProduct.getName())) {
            String slug = productHelper.generateSlug(productDTO.getName(), id);
            existingProduct.setSlug(slug);
        }
        productMapper.updateProductFromDTO(productDTO, existingProduct);

        if (productDTO.getManufacturer() != null && productDTO.getManufacturer().getId() != null) {
            UUID newManufId = productDTO.getManufacturer().getId();
            UUID currentManufId = existingProduct.getManufacturer().getId();
            if (!newManufId.equals(currentManufId)) {
                Manufacturer newManufacturer = manufacturerRepository.findById(newManufId)
                        .orElseThrow(() -> new NotFoundException("Manufacturer not found with id: " + newManufId));
                existingProduct.setManufacturer(newManufacturer);
            }
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return Result.success(productMapper.toDTO(updatedProduct));
    }

    @Cacheable(value = "products", key = "#sku")
    @Override
    public Result<ProductDTO> findBySku(String sku) {
        Product existingProduct = productRepository.findBySku(sku)
                .orElseThrow(() -> new NotFoundException("Product not found with sku:" + sku));
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

    @CacheEvict(value = "products", key = "#id")
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
