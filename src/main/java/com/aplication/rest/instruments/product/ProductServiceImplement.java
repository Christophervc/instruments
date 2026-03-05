package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.ValidationException;
import com.aplication.rest.instruments.core.storage.StorageService;
import com.aplication.rest.instruments.manufacturer.Manufacturer;
import com.aplication.rest.instruments.manufacturer.ManufacturerRepository;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.product.dto.ProductSearchCriteria;
import com.aplication.rest.instruments.product.utils.ProductExcelExporter;
import com.aplication.rest.instruments.product.utils.ProductHelper;
import com.aplication.rest.instruments.product.utils.ProductSpecification;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class ProductServiceImplement implements IProductService {

    private final ManufacturerRepository manufacturerRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductHelper productHelper;
    private final StorageService storageService;

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
    public Result<ProductDTO> save(ProductDTO productDTO, MultipartFile file) {
        if (productDTO.manufacturer() == null || productDTO.manufacturer().id() == null) {
            throw new ValidationException("Manufacturer is mandatory");
        }
        Product product = productMapper.toEntity(productDTO);

        UUID manufacturerId = productDTO.manufacturer().id();
        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
                .orElseThrow(() -> new NotFoundException("Manufacturer not found with id: " + manufacturerId));
        product.setManufacturer(manufacturer);

        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        product.setSlug(productHelper.generateSlug(product.getName(), product.getId()));

        if (productDTO.sku() != null) {
            Optional<Product> existingSku = productRepository.findBySku(productDTO.sku());
            if (existingSku.isPresent()) {
                throw new ValidationException("SKU: " + productDTO.sku() + " already exists ");
            }
            product.setSku(productDTO.sku());
        }

        if (file != null && !file.isEmpty()) {
            String imageUrl = storageService.uploadImage(file);
            product.setImage_url(imageUrl);
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

        if (productDTO.sku() != null && !productDTO.sku().equals(existingProduct.getSku())) {
            Optional<Product> ownerOfSku = productRepository.findBySku(productDTO.sku());
            if (ownerOfSku.isPresent() && !ownerOfSku.get().getId().equals(id)) {
                throw new ValidationException("SKU " + productDTO.sku() + " is already in use");
            }
        }

        if (productDTO.name() != null && !productDTO.name().equals(existingProduct.getName())) {
            String newSlug = productHelper.generateSlug(productDTO.name(), id);
            existingProduct.setSlug(newSlug);
        }

        productMapper.updateProductFromDTO(productDTO, existingProduct);

        if (productDTO.manufacturer() != null && productDTO.manufacturer().id() != null) {
            UUID newManuId = productDTO.manufacturer().id();
            UUID currentManuId = existingProduct.getManufacturer().getId();

            if (!newManuId.equals(currentManuId)) {
                Manufacturer newManufacturer = manufacturerRepository.findById(newManuId)
                        .orElseThrow(() -> new NotFoundException("Manufacturer not found with id: " + newManuId));
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

    @Override
    @Transactional
    @CacheEvict(value = {"products", "products_sku"}, key = "#id")
    public Result<ProductDTO> reduceStock(UUID id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        if (product.getStock() < quantity) {
            throw new ValidationException("Insufficient stock - product: " + product.getName());
        }
        product.setStock(product.getStock() - quantity);
        Product saved = productRepository.save(product);
        return Result.success(productMapper.toDTO(saved));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "products_sku"}, key = "#id")
    public Result<ProductDTO> addStock(UUID id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product no found"));
        product.setStock(product.getStock() + quantity);
        Product saved = productRepository.save(product);
        return Result.success(productMapper.toDTO(saved));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "products_sku"}, key = "#id")
    public Result<ProductDTO> uploadImage(UUID id, MultipartFile file) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product id not found: " + id));

        if (existingProduct.getImage_url() != null) {
            storageService.deleteImage(existingProduct.getImage_url());
        }

        String imageUrl = storageService.uploadImage(file);
        existingProduct.setImage_url(imageUrl);
        Product savedProduct = productRepository.save(existingProduct);
        return Result.success(productMapper.toDTO(savedProduct));
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
