package com.aplication.rest.instruments.service.impl;

import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.controllers.dto.ProductDTO;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.entities.Manufacturer;
import com.aplication.rest.instruments.entities.Product;
import com.aplication.rest.instruments.persistence.IProductDAO;
import com.aplication.rest.instruments.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
public class ProductServiceImplement implements IProductService {
    @Autowired
    private IProductDAO productDAO;

    @Override
    public Result<List<ProductDTO>> findAll(){
        try {
            List<Product> products = productDAO.findAll();
            if(products.isEmpty()) return Result.success(List.of()); //return empty list
            List<ProductDTO> productDTOS = products.stream()
                    .map(product -> ProductDTO.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .type(product.getType())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .manufacturer(product.getManufacturer())
                            .build())
                    .toList();
            return Result.success(productDTOS);
        } catch (Exception e){
            ApiError error = new ApiError("DATABASE_ERROR", "ERROR RETRIEVING PRODUCTS");
            return Result.isFailure(error);
        }
    }

    @Override
    public Result<Optional<ProductDTO>> findById(Long id) {
        Optional<Product> productOptional = productDAO.findById(id);
        if (productOptional.isPresent()){
            Product product = productOptional.get();
            ProductDTO productDTO = ProductDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .type(product.getType())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .manufacturer(product.getManufacturer())
                    .build();
            return Result.success(Optional.of(productDTO));
        }
        else {
            ApiError error = new ApiError("DATABASE_ERROR", "ERROR RETRIEVING PRODUCT OR PRODUCT NOT FOUND");
            return Result.isFailure(error);
        }
    }

    @Override
    public Result<ProductDTO> save(ProductDTO productDTO) {

        if (productDTO.getName().isBlank() ||
                productDTO.getType().isBlank() ||
                productDTO.getDescription().isBlank() ||
                productDTO.getPrice() == null ||
                productDTO.getManufacturer() == null) {
            return Result.isFailure(new ApiError("BAD_REQUEST", "Please enter the following fields: name, type, description, price, manufacturer"));
        }
        Product product = Product.builder()
                .name(productDTO.getName())
                .type(productDTO.getType())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .manufacturer(productDTO.getManufacturer())
                .build();
        Product savedProduct = productDAO.save(product);
        return Result.success(ProductDTO.builder()
                .name(savedProduct.getName())
                .type(savedProduct.getType())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .manufacturer(savedProduct.getManufacturer())
                .build());
    }

    @Override
    public Result<ProductDTO> deleteById(Long id) {
        Product product = productDAO.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: "+id));
        ProductDTO productDTO = ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .type(product.getType())
                .description(product.getDescription())
                .price(product.getPrice())
                .manufacturer(product.getManufacturer())
                .build();
        productDAO.deleteById(id);
        return Result.success(productDTO);
    }

    @Override
    public Result<ProductDTO> update(Long id, ProductDTO productDTO) {
        Optional<Product> productOptional = productDAO.findById(id);
        if (productOptional.isEmpty()) {
            return Result.isFailure(new ApiError("error","Product not found with id: " + id));
        }
        Product product = productOptional.get();
        product.setName(productDTO.getName());
        product.setType(productDTO.getType());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setManufacturer(productDTO.getManufacturer());
        Product updatedProduct = productDAO.save(product);

        return Result.success(ProductDTO.builder()
                .price(updatedProduct.getPrice())
                .name(updatedProduct.getName())
                .type(updatedProduct.getType())
                .description(updatedProduct.getDescription())
                .manufacturer(updatedProduct.getManufacturer()).build()
        );
    }

    @Override
    public List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return productDAO.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public List<Product> findByManufacturer(Manufacturer manufacturer) {
        return productDAO.findByManufacturer(manufacturer);
    }

    @Override
    public List<Product> findAllSortedByNameAsc() {
        return productDAO.findAllSortedByNameAsc();
    }

    @Override
    public List<Product> findAllSortedByNameDesc() {
        return productDAO.findAllSortedByNameDesc();
    }
}
