package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.manufacturer.Manufacturer;
import com.aplication.rest.instruments.manufacturer.ManufacturerRepository;
import com.aplication.rest.instruments.manufacturer.dto.ManufacturerDTO;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.product.utils.ProductHelper;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private ManufacturerRepository manufacturerRepository;
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
        UUID manufacturerId = UUID.randomUUID();
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder()
                .id(manufacturerId)
                .build();

        ProductDTO inputDTO = ProductDTO.builder()
                .name("Fender Strat")
                .sku("SKU-123")
                .manufacturer(manufacturerDTO)
                .build();

        Manufacturer manufacturerEntity = Manufacturer.builder()
                .id(manufacturerId)
                .name("Fender")
                .build();

        Product productEntity = Product.builder()
                .id(UUID.randomUUID())
                .name("Fender Strat")
                .build();

        when(manufacturerRepository.findById(manufacturerId)).thenReturn(Optional.of(manufacturerEntity));
        when(productMapper.toEntity(inputDTO)).thenReturn(productEntity);
        // Helper (name, ID)
        when(productHelper.generateSlug(anyString(), any(UUID.class))).thenReturn("fender-strat-123");
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(productMapper.toDTO(any(Product.class))).thenReturn(inputDTO);

        // Act
        Result<ProductDTO> result = productService.save(inputDTO);

        // Assert
        assertTrue(result.isSuccess());
        verify(productRepository).save(any(Product.class));
        verify(productHelper).generateSlug(eq("Fender Strat"), any(UUID.class));
    }

    // --- TEST 2: Soft Delete ---
    @Test
    void deleteById_ShouldSoftDelete_WhenProductExists() {
        UUID id = UUID.randomUUID();
        Product existingProduct = Product.builder().id(id).name("Guitar").active(true).build();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        productService.deleteById(id);

        verify(productRepository).save(argThat(product -> !product.getActive()));
    }

    // --- TEST 3: Reduce Stock Successfully ---
    @Test
    void reduceStock_ShouldDecreaseStock_WhenSufficient() {
        UUID id = UUID.randomUUID();
        Product product = Product.builder().id(id).stock(10).build();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(productMapper.toDTO(any(Product.class))).thenReturn(ProductDTO.builder().stock(8).build());

        Result<ProductDTO> result = productService.reduceStock(id, 2);

        assertTrue(result.isSuccess());
        assertEquals(8, product.getStock());
        verify(productRepository).save(product);
    }

    // --- TEST 4: Reduce Stock Fails (Insufficient) ---
    @Test
    void reduceStock_ShouldThrowException_WhenStockIsInsufficient() {
        UUID id = UUID.randomUUID();
        Product product = Product.builder().id(id).stock(1).build();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        assertThrows(ValidationException.class, () -> productService.reduceStock(id, 5));
        verify(productRepository, never()).save(any());
    }

    // --- TEST 5: Add Stock ---
    @Test
    void addStock_ShouldIncreaseStock() {
        UUID id = UUID.randomUUID();
        Product product = Product.builder().id(id).stock(5).build();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        productService.addStock(id, 3);

        assertEquals(8, product.getStock());
        verify(productRepository).save(product);
    }
    // --- TEST 6: Error handling (Not Found) ---
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

    // --- TEST 7: Should update if exists ---
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
