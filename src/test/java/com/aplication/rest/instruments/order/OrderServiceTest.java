package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderItemRequest;
import com.aplication.rest.instruments.order.dto.OrderRequest;
import com.aplication.rest.instruments.order.enums.OrderStatus;
import com.aplication.rest.instruments.product.Product;
import com.aplication.rest.instruments.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private ProductRepository productRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    // --- TEST 1: Create order reduces stock when it is sufficient ---
    @Test
    void createOrder_ShouldReduceStock_WhenStockIsSufficient() {
        // Arrange
        UUID productId = UUID.randomUUID();
        // customer request 2 guitars
        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(productId, 2)));
        // There are 10 in stock
        Product product = Product.builder().id(productId).name("Guitar").stock(10).price(BigDecimal.TEN).build();
        //simulate that the product exists
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toDTO(any(Order.class))).thenReturn(OrderDTO.builder().build());
        // Act
        orderService.createOrder(request);
        // Assert
        // verify the stock logic: 10 - 2 = 8
        assertEquals(8, product.getStock());
        // Verify save the updated product
        verify(productRepository).save(product);
        // Verify saved order
        verify(orderRepository).save(any(Order.class));
    }

    // ---TEST 2 : Throws exception when stock is insufficient---
    @Test
    void createOrder_ShouldThrowException_WhenStockIsInsufficient() {
        // Arrange

        UUID productId = UUID.randomUUID();
        // customer request 5 guitars
        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(productId, 5)));
        // There are 2 in stock
        Product product = Product.builder().id(productId).name("Guitar").stock(2).build();
        // product exists
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.createOrder(request));
        // verify the order, never was saved
        verify(orderRepository, never()).save(any());
        //verify the stock has not been reduced and has not changed.
        assertEquals(2, product.getStock());
    }
    // --- TEST 3: Cancel order should returns stock ---
    @Test
    void cancelOrder_ShouldRestockProduct_WhenOrderIsCancelled() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        // there are 5 products in stock
        Product product = Product.builder().id(productId).stock(5).build();

        // An existing order that purchased 3 units of this product
        OrderItem item = OrderItem.builder().product(product).quantity(3).build();
        Order order = Order.builder().id(orderId).status(OrderStatus.PENDING).items(List.of(item)).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toDTO(any(Order.class))).thenReturn(OrderDTO.builder().status(OrderStatus.CANCELLED).build());

        // Act
        orderService.cancelOrder(orderId);

        // Assert
        // 5 (current stock) + 3 (returning) = 8
        assertEquals(8, product.getStock());
        verify(productRepository).save(product);
        // order status was cancelled
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    // --- TEST 4: Create order should calculate total ---
    @Test
    void createOrder_ShouldCalculateTotalCorrectly() {
        // --- ARRANGE ---
        UUID p1Id = UUID.randomUUID();
        UUID p2Id = UUID.randomUUID();

        // customer request: 2 of product 1: P1 & 1 of product 2: P2
        OrderRequest request = new OrderRequest(List.of(
                new OrderItemRequest(p1Id, 2),
                new OrderItemRequest(p2Id, 1)
        ));

        // P1 costs 1000, P2 costs 500
        Product p1 = Product.builder().id(p1Id).price(new BigDecimal("1000.00")).stock(10).build();
        Product p2 = Product.builder().id(p2Id).price(new BigDecimal("500.00")).stock(10).build();
        // products exists
        when(productRepository.findById(p1Id)).thenReturn(Optional.of(p1));
        when(productRepository.findById(p2Id)).thenReturn(Optional.of(p2));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        when(orderMapper.toDTO(any(Order.class))).thenReturn(OrderDTO.builder().build());

        // --- ACT ---
        orderService.createOrder(request);

        // --- ASSERT ---
        // using ArgumentCaptor to “steal” the order that was attempted to be saved
        var orderCaptor = org.mockito.ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        // expected: (1000 * 2) + (500 * 1) = 2500
        assertEquals(new BigDecimal("2500.00"), savedOrder.getTotal());
    }

    // --- TEST 5: Update order state if its valid ---
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

    // --- TEST 6: update order state when is not valid ---
    @Test
    void updateStatus_ShouldThrowException_WhenOrderIsAlreadyCancelled() {
        // --- ARRANGE ---
        UUID orderId = UUID.randomUUID();
        // an existing cancelled order
        Order cancelledOrder = Order.builder().id(orderId).status(OrderStatus.CANCELLED).build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(cancelledOrder));
        // --- ACT & ASSERT ---
        assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(orderId, OrderStatus.PENDING);
        });
        verify(orderRepository, never()).save(any());
    }

}
