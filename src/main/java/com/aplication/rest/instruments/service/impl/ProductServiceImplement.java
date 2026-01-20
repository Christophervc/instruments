package com.aplication.rest.instruments.service.impl;

import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.controllers.dto.ProductDTO;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.entities.Product;
import com.aplication.rest.instruments.mapper.ProductMapper;
import com.aplication.rest.instruments.repository.ProductRepository;
import com.aplication.rest.instruments.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class ProductServiceImplement implements IProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result<List<ProductDTO>> findAll(){
        try {
            List<Product> products = (List<Product>) productRepository.findAll();
            if(products.isEmpty()) return Result.success(List.of()); //return empty list
            return Result.success(productMapper.toDTOList(products));
        } catch (Exception e){
            return Result.isFailure(new ApiError("DATABASE_ERROR", "ERROR RETRIEVING PRODUCTS"));
        }
    }

    @Override
    public Result<Optional<ProductDTO>> findById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()){
            Product product = productOptional.get();
            ProductDTO productDTO = productMapper.toDTO(product);
            return Result.success(Optional.of(productDTO));
        }
        else {
            return Result.isFailure(new ApiError("NOT_FOUND", "PRODUCT NOT FOUND"));
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

        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        ProductDTO savedProductDTO = productMapper.toDTO(savedProduct);
        return Result.success(savedProductDTO);
    }

    @Override
    public Result<ProductDTO> update(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        productMapper.updateProductFromDTO(productDTO, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return Result.success(productMapper.toDTO(updatedProduct));
    }

    @Override
    public Result<ProductDTO> deleteById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: "+id));
        ProductDTO productDTO = productMapper.toDTO(product);
        productRepository.deleteById(id);
        return Result.success(productDTO);
    }

    /*@Override
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
    }*/
}
