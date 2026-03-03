package com.aplication.rest.instruments.order.dto;

import com.aplication.rest.instruments.order.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderDTO(
        UUID id,
        CustomerInfoDTO customer,
        LocalDateTime createdAt,
        BigDecimal total,
        OrderStatus status,
        List<OrderItemDTO> items
) {
}
