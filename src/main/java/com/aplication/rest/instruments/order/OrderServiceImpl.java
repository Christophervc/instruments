package com.aplication.rest.instruments.order;

import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.order.dto.OrderDTO;
import com.aplication.rest.instruments.order.dto.OrderItemRequest;
import com.aplication.rest.instruments.order.dto.OrderRequest;
import com.aplication.rest.instruments.order.dto.OrderSearchCriteria;
import com.aplication.rest.instruments.order.enums.OrderStatus;
import com.aplication.rest.instruments.order.utils.OrderSpecification;
import com.aplication.rest.instruments.product.IProductService;
import com.aplication.rest.instruments.product.Product;
import com.aplication.rest.instruments.product.ProductRepository;
import com.aplication.rest.instruments.product.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final IProductService productService;

    @Override
    @Transactional
    public Result<OrderDTO> createOrder(OrderRequest request) {

        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.products()) {
            Result<ProductDTO> result = productService.reduceStock(itemRequest.productId(), itemRequest.quantity());
            ProductDTO productDTO = result.data();
            Product productRef = productRepository.getReferenceById(productDTO.getId());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(productRef)
                    .productName(productDTO.getName())//snapshot product name
                    .quantity(itemRequest.quantity())
                    .price(productDTO.getPrice())//snapshot product price
                    .build();
            orderItems.add(orderItem);


            BigDecimal subtotal = productDTO.getPrice().multiply(new BigDecimal(itemRequest.quantity()));
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
            productService.addStock(item.getProduct().getId(), item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return Result.success(orderMapper.toDTO(savedOrder));
    }


    @Override
    public Result<OrderDTO> findById(UUID id) {
        return orderRepository.findById(id).map(order -> Result.success(orderMapper.toDTO(order)))
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
    }

    @Override
    public Result<Page<OrderDTO>> findAll(Pageable pageable, OrderSearchCriteria criteria) {
        Specification<Order> spec = OrderSpecification.fromCriteria(criteria);
        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);
        Page<OrderDTO> dtoOrdersPage = ordersPage.map(orderMapper::toDTO);
        return Result.success(dtoOrdersPage);
    }

    @Override
    public Result<OrderDTO> updateOrderStatus(UUID id, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED) {
            return this.cancelOrder(id);
        }
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order status cannot be updated when it is cancelled");
        }
        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        return Result.success(orderMapper.toDTO(savedOrder));
    }
}
