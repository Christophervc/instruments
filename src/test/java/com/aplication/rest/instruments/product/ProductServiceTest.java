package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.ValidationException;
import com.aplication.rest.instruments.manufacturer.Manufacturer;
import com.aplication.rest.instruments.manufacturer.ManufacturerMapper;
import com.aplication.rest.instruments.manufacturer.ManufacturerRepository;
import com.aplication.rest.instruments.manufacturer.dto.ManufacturerDTO;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.product.utils.ProductHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@ExtendWith(MockitoExtension.class)
class ProductServiceImplementTest {

    @Mock private ManufacturerRepository manufacturerRepository;
    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;
    @Mock private ProductHelper productHelper;

    @InjectMocks
    private ProductServiceImplement productService;

    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);
        product.setName("Guitarra Test");
        product.setStock(5);
        product.setActive(true);
    }

    @Test
    @DisplayName("1. Should save a product successfully")
    void testSaveProduct_Success() {
        // Arrange (Preparar)
        UUID manufacturerId = UUID.randomUUID();
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder()
                .id(manufacturerId)
                .build();

        // crear un producto completo con marca
        ProductDTO inputDto = ProductDTO.builder()
                .id(productId)
                .name("Guitarra Test")
                .stock(5)
                .manufacturer(manufacturerDTO)
                .build();
        Manufacturer manufacturerEntity = new Manufacturer();
        manufacturerEntity.setId(manufacturerId);

        // Simulamos el comportamiento de los Mocks
        when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(product);
        when(manufacturerRepository.findById(manufacturerDTO.id())).thenReturn(Optional.of(manufacturerEntity));
        when(productHelper.generateSlug(anyString(), any(UUID.class))).thenReturn("guitarra-test");
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(any(Product.class))).thenReturn(inputDto);

        // Act (Ejecutar) usando nuestro inputDto completo
        Result<ProductDTO> result = productService.save(inputDto, null);

        // Assert (Comprobar)
        assertTrue(result.isSuccess());
        assertNotNull(result.data());
        assertEquals("Guitarra Test", result.data().name());

        // Verificamos que se haya llamado a save del repositorio
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("2. Should Soft Delete (switch active to false)")
    void testDeleteById_SoftDelete_Success() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Creamos un DTO simulando que está inactivo para el retorno
        ProductDTO deletedDTO = ProductDTO.builder().active(false).build();
        when(productMapper.toDTO(any(Product.class))).thenReturn(deletedDTO);

        // Act
        Result<ProductDTO> result = productService.deleteById(productId);

        // Assert
        assertTrue(result.isSuccess());
        assertFalse(result.data().active()); // Comprobamos que el DTO devuelto dice false
        assertFalse(product.getActive()); // Comprobamos que a la entidad original se le cambió a false

        // Verificamos que guardó el cambio en BD
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("3. Should reduce stock when quantity is enough")
    void testReduceStock_Success() {
        // Arrange
        int quantityToReduce = 2; // Stock actual es 5
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO updatedDTO = ProductDTO.builder().stock(3).build();
        when(productMapper.toDTO(any(Product.class))).thenReturn(updatedDTO);

        // Act
        Result<ProductDTO> result = productService.reduceStock(productId, quantityToReduce);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(3, product.getStock()); // 5 - 2 = 3
        assertEquals(3, result.data().stock());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("4. Should throw exception when stock is insufficient")
    void testReduceStock_Fails_WhenInsufficientStock() {
        // Arrange
        int quantityToReduce = 10; // Stock actual es 5, queremos quitar 10
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        // Comprobamos que lance exactamente la excepción ValidationException
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            productService.reduceStock(productId, quantityToReduce);
        });

        // Verificamos el mensaje de error
        assertTrue(exception.getMessage().contains("Insufficient stock"));

        // Comprobamos que NUNCA se haya llamado a save (no se guardó nada erróneo en BD)
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("5. Should add stock successfully")
    void testAddStock_Success() {
        // Arrange
        int quantityToAdd = 5; // Stock actual es 5
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO updatedDTO = ProductDTO.builder().stock(10).build();
        when(productMapper.toDTO(any(Product.class))).thenReturn(updatedDTO);

        // Act
        Result<ProductDTO> result = productService.addStock(productId, quantityToAdd);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(10, product.getStock()); // 5 + 5 = 10
        assertEquals(10, result.data().stock());
        verify(productRepository, times(1)).save(product);
    }
}