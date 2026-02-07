package com.aplication.rest.instruments.order.dto;

import com.aplication.rest.instruments.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderDTO {
    private UUID id;
    private LocalDateTime createdAt;
    private BigDecimal total;
    private OrderStatus status;
    private List<OrderItemDTO> items;
}
