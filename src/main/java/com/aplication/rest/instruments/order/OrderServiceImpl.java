package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderRequest;
import com.aplication.rest.instruments.order.enums.OrderStatus;
import com.aplication.rest.instruments.product.Product;
import com.aplication.rest.instruments.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public Result<OrderDTO> createOrder(OrderRequest request) {

        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var itemRequest : request.products()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id: " + itemRequest.productId()));

            if (product.getStock() < itemRequest.quantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }

            product.setStock(product.getStock() - itemRequest.quantity());
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.quantity())
                    .price(product.getPrice())
                    .build();
            orderItems.add(item);

            BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(itemRequest.quantity()));

            total = total.add(subtotal);
        }
        order.setItems(orderItems);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        return Result.success(orderMapper.toDTO(savedOrder));
    }

    @Override
    public Result<OrderDTO> cancelOrder(UUID uuid) {
        Order order = orderRepository.findById(uuid).orElseThrow(() -> new NotFoundException("Order not found"));
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return Result.success(orderMapper.toDTO(savedOrder));
    }

    @Override
    public Result<OrderDTO> findById(UUID id) {
        return orderRepository.findById(id).map(order -> Result.success(orderMapper.toDTO(order)))
                .orElseThrow(() -> new NotFoundException("Order not found - id: " + id));
    }

    @Override
    public Result<Page<OrderDTO>> findAll(Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findAll(pageable);
        Page<OrderDTO> dtoPage = ordersPage.map(orderMapper::toDTO);
        return Result.success(dtoPage);
    }
}
