package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.product.utils.ProductHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 1. Enable Mockito
public class ProductServiceTest {
    // 2. Prepare Mocks
    //Simulate the external dependencies of the service
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductHelper productHelper; // Note! we use this in save

    // 3. INJECT MOCKS
    // Mockito instance service & inject mocks
    @InjectMocks
    private ProductServiceImplement productService;

    // --- TEST 1: save product successfully ---
    @Test
    void save_ShouldReturnSuccess_WhenProductIsValid() {
        // --- ARRANGE (prepare) ---
        ProductDTO inputDTO = ProductDTO.builder().name("Fender Strat").price(BigDecimal.valueOf(1000)).build();
        Product productEntity = Product.builder().name("Fender Strat").build();
        Product savedEntity = Product.builder().id(UUID.randomUUID()).name("Fender Strat").sku("SKU-123").build();
        ProductDTO outputDTO = ProductDTO.builder().id(savedEntity.getId()).name("Fender Strat").sku("SKU-123").build();

        // Training Mocks: "When ... then ..."
        when(productMapper.toEntity(inputDTO)).thenReturn(productEntity);
        when(productHelper.generateSku(any(Product.class))).thenReturn("SKU-123");
        when(productHelper.generateSlug(anyString())).thenReturn("fender-strat");
        when(productRepository.save(any(Product.class))).thenReturn(savedEntity);
        when(productMapper.toDTO(savedEntity)).thenReturn(outputDTO);

        // --- ACT (Action) ---
        Result<ProductDTO> result = productService.save(inputDTO);

        // --- ASSERT (verify) ---
        assertTrue(result.isSuccess()); // verify is success
        assertNotNull(result.data().getId()); // Verify that it has an ID
        assertEquals("SKU-123", result.data().getSku()); // Verify data

        // Verify that repository was called one time
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // --- TEST 2: Soft Delete ---
    @Test
    void deleteById_ShouldSoftDelete_WhenProductExists() {
        // --- ARRANGE ---
        UUID id = UUID.randomUUID();
        // Create an active product (true)
        Product existingProduct = Product.builder().id(id).name("Guitar").active(true).build();

        // Simulate that it is found
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        // Simulate saving (same object modified)
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Simulate mapper (to avoid NullPointer when returning)
        when(productMapper.toDTO(any(Product.class))).thenReturn(new ProductDTO());

        // --- ACT ---
        productService.deleteById(id);

        // --- ASSERT ---
        // verify to inspect what was attempted to be saved
        verify(productRepository).save(argThat(product ->
                product.getId().equals(id) &&
                        !product.getActive() // <--- should be false
        ));
    }

    // --- TEST 3: Error handling (Not Found) ---
    @Test
    void findById_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        // --- ARRANGE ---
        UUID id = UUID.randomUUID();
        // DB returns empty
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        // Verify exception
        assertThrows(NotFoundException.class, () -> {
            productService.findById(id);
        });

        // Verify mapper is NEVER called (because it failed before).
        verify(productMapper, never()).toDTO(any());
    }

    // --- TEST 4: Should update if exists ---

    @Test
    void update_ShouldUpdateProduct_WhenProductExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductDTO updateDTO = ProductDTO.builder().name("New Name").build();
        Product existingProduct = Product.builder().id(id).name("Old Name").build();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(productMapper.toDTO(any(Product.class))).thenReturn(updateDTO);

        // Act
        productService.update(id, updateDTO);

        // Assert
        // verify update mapper was called
        verify(productMapper).updateProductFromDTO(updateDTO, existingProduct);
        // verify save existing product
        verify(productRepository).save(existingProduct);
    }
}
