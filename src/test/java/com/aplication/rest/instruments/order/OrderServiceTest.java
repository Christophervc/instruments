package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.ValidationException;
import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderItemRequest;
import com.aplication.rest.instruments.order.dto.OrderRequest;
import com.aplication.rest.instruments.order.enums.OrderStatus;
import com.aplication.rest.instruments.product.IProductService;
import com.aplication.rest.instruments.product.Product;
import com.aplication.rest.instruments.product.ProductRepository;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private IProductService productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    // --- TEST 1: Create order with Snapshot and Service Delegation ---
    @Test
    void createOrder_ShouldCreateAndCalculateTotal_WhenStockIsSufficient() {
        // Arrange
        // customer request 2 guitars
        //mock request -list of order items
        UUID productId = UUID.randomUUID();
        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(productId, 2)));

        // Product service should return (Price 1000)
        ProductDTO mockedProductDTO = ProductDTO.builder()
                .id(productId)
                .name("Fender Stratocaster") // test the name snapshot
                .price(new BigDecimal("1000.00"))
                .build();

        // 1. Mock ProductService reduceStock
        when(productService.reduceStock(productId, 2)).thenReturn(Result.success(mockedProductDTO));

        // 2. Mock JPA product repository  reference
        Product productRef = new Product();
        productRef.setId(productId);
        when(productRepository.getReferenceById(productId)).thenReturn(productRef);

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toDTO(any(Order.class))).thenReturn(OrderDTO.builder().build());

        // Act
        orderService.createOrder(request);

        // Assert
        // using ArgumentCaptor to “steal” the order that was attempted to be saved
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        // Verify Total (1000 * 2)
        assertEquals(new BigDecimal("2000.00"), savedOrder.getTotal());

        // Verify Snapshot pattern (name was copied to the order item)
        OrderItem savedItem = savedOrder.getItems().getFirst();
        assertEquals("Fender Stratocaster", savedItem.getProductName());
        assertEquals(new BigDecimal("1000.00"), savedItem.getPrice());
    }

    // --- TEST 2: createOrder Exception from ProductService ---
    @Test
    void createOrder_ShouldThrowException_WhenProductServiceFails() {
        // Arrange
        UUID productId = UUID.randomUUID();
        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(productId, 5)));

        // Simulate ProductService reduceStock failure (ej. no hay stock)
        when(productService.reduceStock(productId, 5))
                .thenThrow(new ValidationException("Insufficient stock"));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            orderService.createOrder(request);
        });

        assertEquals("Insufficient stock", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    // --- TEST 3: Cancel Order restores stock via Service ---
    @Test
    void cancelOrder_ShouldRestockProduct_WhenOrderIsCancelled() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product productRef = Product.builder().id(productId).build();
        OrderItem item = OrderItem.builder().product(productRef).quantity(3).build();
        Order order = Order.builder().id(orderId).status(OrderStatus.PENDING).items(List.of(item)).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toDTO(any(Order.class))).thenReturn(OrderDTO.builder().status(OrderStatus.CANCELLED).build());

        // Act
        orderService.cancelOrder(orderId);

        // Assert
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        // verify that ProductService was asked to return 3 units
        verify(productService).addStock(productId, 3);
        verify(orderRepository).save(order);
    }

    // --- TEST 4: Update order state if its valid ---
    @Test
    void updateStatus_ShouldAdvanceStatus_WhenTransitionIsValid() {
        // --- ARRANGE ---
        UUID orderId = UUID.randomUUID();
        // current order state = PENDING
        Order existingOrder = Order.builder().id(orderId).status(OrderStatus.PENDING).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toDTO(any(Order.class))).thenReturn(OrderDTO.builder().build());

        // --- ACT ---
        // Switch from pending to paid
        orderService.updateOrderStatus(orderId, OrderStatus.PAID);

        // --- ASSERT ---
        verify(orderRepository).save(argThat(order ->
                order.getStatus() == OrderStatus.PAID
        ));
    }

    // --- TEST 5: update order state when is not valid ---
    @Test
    void updateStatus_ShouldThrowException_WhenOrderIsAlreadyCancelled() {
        // --- ARRANGE ---
        UUID orderId = UUID.randomUUID();
        // an existing canceled order
        Order cancelledOrder = Order.builder().id(orderId).status(OrderStatus.CANCELLED).build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(cancelledOrder));
        // --- ACT & ASSERT ---
        assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(orderId, OrderStatus.PENDING);
        });
        verify(orderRepository, never()).save(any());
    }
}
