package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderRequest;
import com.aplication.rest.instruments.product.Product;
import com.aplication.rest.instruments.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

        for (var itemRequest : request.products()){
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(()-> new NotFoundException("Product not found with id: " + itemRequest.productId()));

            if (product.getStock()< itemRequest.quantity()){
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
        Order savedOrder = orderRepository.save(order);
        return Result.success(orderMapper.toDTO(savedOrder));
    }
}
