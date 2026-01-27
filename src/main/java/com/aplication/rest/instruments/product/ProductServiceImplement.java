package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
    public Result<Optional<ProductDTO>> findById(UUID id) {
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
        Product product = productMapper.toEntity(productDTO);
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
    public Result<ProductDTO> deleteById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: "+id));
        ProductDTO productDTO = productMapper.toDTO(product);
        productRepository.deleteById(id);
        return Result.success(productDTO);
    }
}
