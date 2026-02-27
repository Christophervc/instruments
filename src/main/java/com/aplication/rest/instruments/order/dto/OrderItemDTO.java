package com.aplication.rest.instruments.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemDTO(
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal
) {
}
